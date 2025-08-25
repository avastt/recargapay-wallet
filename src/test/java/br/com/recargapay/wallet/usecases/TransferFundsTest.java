package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.enums.TransactionType;
import br.com.recargapay.wallet.exception.InsufficientFundsException;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.repository.WalletRepository;
import br.com.recargapay.wallet.usecases.GetUserWallet;
import br.com.recargapay.wallet.usecases.TransferFunds;
import br.com.recargapay.wallet.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransferFundsTest {

	@Mock
	private WalletRepository walletRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private GetUserWallet getUserWallet;

	@Mock
	private MessageUtils messageUtils;

	@InjectMocks
	private TransferFunds transferFunds;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldTransferFundsSuccessfully() {
		// GIVEN
		UUID fromWalletId = UUID.randomUUID();
		UUID toWalletId = UUID.randomUUID();
		BigDecimal transferAmount = BigDecimal.valueOf(100);

		Wallet fromWallet = Wallet.builder()
				.id(fromWalletId)
				.walletName("Source Wallet")
				.currency(Currency.getInstance("USD"))
				.balance(BigDecimal.valueOf(200))
				.build();

		Wallet toWallet = Wallet.builder()
				.id(toWalletId)
				.walletName("Destination Wallet")
				.currency(Currency.getInstance("USD"))
				.balance(BigDecimal.valueOf(50))
				.build();

		when(getUserWallet.execute(fromWalletId)).thenReturn(fromWallet);
		when(getUserWallet.execute(toWalletId)).thenReturn(toWallet);

		// WHEN
		Wallet updatedFromWallet = transferFunds.execute(fromWalletId, toWalletId, transferAmount);

		// THEN
		assertEquals(BigDecimal.valueOf(100), updatedFromWallet.getBalance());
		assertEquals(BigDecimal.valueOf(150), toWallet.getBalance());

		// Verify wallet updates
		verify(walletRepository, times(2)).save(any(WalletModel.class));

		// Verify transactions
		verify(transactionRepository, times(1)).save(argThat(transaction ->
				transaction.getType() == TransactionType.TRANSFER_OUT &&
						transaction.getWallet().getId().equals(fromWalletId) &&
						transaction.getAmount().compareTo(transferAmount) == 0 &&
						transaction.getHistoricalBalance().compareTo(BigDecimal.valueOf(100)) == 0
		));

		verify(transactionRepository, times(1)).save(argThat(transaction ->
				transaction.getType() == TransactionType.TRANSFER_IN &&
						transaction.getWallet().getId().equals(toWalletId) &&
						transaction.getAmount().compareTo(transferAmount) == 0 &&
						transaction.getHistoricalBalance().compareTo(BigDecimal.valueOf(150)) == 0
		));
	}

	@Test
	void shouldThrowInsufficientFundsExceptionWhenBalanceIsLow() {
		// GIVEN
		UUID fromWalletId = UUID.randomUUID();
		UUID toWalletId = UUID.randomUUID();
		BigDecimal transferAmount = BigDecimal.valueOf(300);

		Wallet fromWallet = Wallet.builder()
				.walletName("Source Wallet")
				.currency(Currency.getInstance("USD"))
				.balance(BigDecimal.valueOf(200))
				.build();

		Wallet toWallet = Wallet.builder()
				.walletName("Destination Wallet")
				.currency(Currency.getInstance("USD"))
				.balance(BigDecimal.valueOf(50))
				.build();

		when(getUserWallet.execute(fromWalletId)).thenReturn(fromWallet);
		when(getUserWallet.execute(toWalletId)).thenReturn(toWallet);
		when(messageUtils.getMessage(any())).thenReturn("Insufficient funds");

		// WHEN & THEN
		InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () ->
				transferFunds.execute(fromWalletId, toWalletId, transferAmount)
		);

		assertEquals("Insufficient funds", exception.getMessage());
		verify(walletRepository, never()).save(any(WalletModel.class));
		verify(transactionRepository, never()).save(any(TransactionModel.class));
	}
}
