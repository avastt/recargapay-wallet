package br.com.recargapay.wallet.usecases;


import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.enums.TransactionType;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.repository.WalletRepository;
import br.com.recargapay.wallet.usecases.DepositAmount;
import br.com.recargapay.wallet.usecases.GetUserWallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


class DepositAmountTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private GetUserWallet getUserWallet;

    @InjectMocks
    private DepositAmount depositAmount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldDepositAmountSuccessfully() {
        // GIVEN
        UUID walletId = UUID.randomUUID();
        BigDecimal depositAmountValue = BigDecimal.valueOf(100);

        Wallet wallet = Wallet.builder()
                .walletName("Test Wallet")
                .userName("Test User")
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200))
                .build();
        when(getUserWallet.execute(walletId)).thenReturn(wallet);

        // WHEN
        Wallet updatedWallet = depositAmount.execute(walletId, depositAmountValue);

        // THEN
        assertEquals(BigDecimal.valueOf(300), updatedWallet.getBalance());
        verify(walletRepository, times(1)).save(any(WalletModel.class));
        verify(transactionRepository, times(1)).save(any(TransactionModel.class));
        verify(transactionRepository).save(argThat(transaction ->
                transaction.getType() == TransactionType.DEPOSIT &&
                        transaction.getHistoricalBalance().equals(BigDecimal.valueOf(300)) &&
                        transaction.getAmount().equals(depositAmountValue)
        ));
    }
}