package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.enums.TransactionType;
import br.com.recargapay.wallet.exception.InsufficientFundsException;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.repository.WalletRepository;
import br.com.recargapay.wallet.usecases.GetUserWallet;
import br.com.recargapay.wallet.usecases.WithdrawAmount;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WithdrawAmountTest {

    @Mock
    private GetUserWallet getUserWallet;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MessageUtils messageUtils;

    @InjectMocks
    private WithdrawAmount withdrawAmount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldWithdrawAmountSuccessfully() {
        // GIVEN
        UUID walletId = UUID.randomUUID();
        BigDecimal withdrawAmountValue = BigDecimal.valueOf(50);

        Wallet wallet = Wallet.builder()
                .walletName("Test Wallet")
                .userName("Test User")
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200))
                .build();
        when(getUserWallet.execute(walletId)).thenReturn(wallet);

        // WHEN
        Wallet updatedWallet = withdrawAmount.execute(walletId, withdrawAmountValue);

        // THEN
        assertEquals(BigDecimal.valueOf(150), updatedWallet.getBalance());
        verify(walletRepository, times(1)).save(any(WalletModel.class));
        verify(transactionRepository, times(1)).save(any(TransactionModel.class));
        verify(transactionRepository).save(argThat(transaction ->
                transaction.getType() == TransactionType.WITHDRAW &&
                        transaction.getHistoricalBalance().equals(BigDecimal.valueOf(150)) &&
                        transaction.getAmount().equals(withdrawAmountValue)
        ));
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenBalanceIsLow() {
        // GIVEN
        UUID walletId = UUID.randomUUID();
        BigDecimal withdrawAmountValue = BigDecimal.valueOf(300);

        Wallet wallet = Wallet.builder()
                .walletName("Test Wallet")
                .userName("Test User")
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200))
                .build();
        when(getUserWallet.execute(walletId)).thenReturn(wallet);
        when(messageUtils.getMessage(any())).thenReturn("Insufficient funds");

        // WHEN & THEN
        assertThrows(InsufficientFundsException.class, () ->
                withdrawAmount.execute(walletId, withdrawAmountValue)
        );
        verify(walletRepository, never()).save(any(WalletModel.class));
        verify(transactionRepository, never()).save(any(TransactionModel.class));
    }
}