package br.com.recargapay.wallet.domains;

import br.com.recargapay.wallet.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Transaction {

    private UUID id;
    private Wallet wallet;
    private BigDecimal amount;
    private TransactionType type;
    private BigDecimal historicalBalance;
    private LocalDateTime createdAt;
}
