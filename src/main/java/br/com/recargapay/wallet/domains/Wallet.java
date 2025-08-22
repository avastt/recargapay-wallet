package br.com.recargapay.wallet.domains;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Wallet {

    private UUID id;
    private String walletName;
    private String userName;
    private Currency currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
