package br.com.recargapay.wallet.models;

import br.com.recargapay.wallet.domains.Transaction;
import br.com.recargapay.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class TransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletModel wallet;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal historicalBalance;
    private LocalDateTime createdAt;

    public TransactionModel(final Transaction transaction) {
        this.id = transaction.getId();
        this.wallet = ofNullable(transaction.getWallet())
                .map(WalletModel::new).orElse(null);
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.historicalBalance = transaction.getHistoricalBalance();
        this.createdAt = transaction.getCreatedAt();
    }

    public Transaction toDomain(){
        return Transaction.builder()
                .id(this.id)
                .wallet(ofNullable(this.wallet)
                        .map(WalletModel::toDomain)
                        .orElse(null))
                .amount(this.amount)
                .type(this.type)
                .historicalBalance(this.historicalBalance)
                .createdAt(this.createdAt)
                .build();
    }
}