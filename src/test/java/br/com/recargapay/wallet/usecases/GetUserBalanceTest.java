package br.com.recargapay.wallet.usecases;


import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.usecases.GetUserBalance;
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
import static org.mockito.Mockito.when;

class GetUserBalanceTest {

    @Mock
    private GetUserWallet getUserWallet;

    @InjectMocks
    private GetUserBalance getUserBalance;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetUserBalanceSuccessfully() {
        UUID id = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .walletName("Test Wallet")
                .userName("Test User")
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.TEN)
                .build();

        WalletModel walletModel = new WalletModel(wallet);
        when(getUserWallet.execute(id)).thenReturn(walletModel.toDomain());

        BigDecimal balance = getUserBalance.execute(id);

        assertEquals(wallet.getBalance(), balance);
    }
}