package br.com.recargapay.wallet.usecases;


import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.WalletRepository;
import br.com.recargapay.wallet.usecases.CreateUserWallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
class CreateUserWalletTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private CreateUserWallet createUserWallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserWalletSuccessfully() {
        Wallet wallet = Wallet.builder()
                .walletName("Test Wallet")
                .userName("Test User")
                .currency(Currency.getInstance("USD"))
                .balance(null)
                .build();

        WalletModel walletModel = new WalletModel(wallet);
        when(walletRepository.save(any(WalletModel.class))).thenReturn(walletModel);

        Wallet result = createUserWallet.execute(wallet);

        assertEquals(wallet.getWalletName(), result.getWalletName());
        assertEquals(wallet.getUserName(), result.getUserName());
        assertEquals(wallet.getCurrency(), result.getCurrency());
        assertEquals(wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance(), result.getBalance());
    }
}