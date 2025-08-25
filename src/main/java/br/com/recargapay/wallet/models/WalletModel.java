package br.com.recargapay.wallet.models;

import br.com.recargapay.wallet.domains.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
public class WalletModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
    private String walletName;
    private String userName;
    private Currency currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    @Version
    private Long version;

    public WalletModel(final Wallet wallet) {
        this.id = wallet.getId();
        this.balance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
        this.walletName = wallet.getWalletName();
        this.userName = wallet.getUserName();
        this.currency = wallet.getCurrency();
        this.createdAt = LocalDateTime.now();
    }

    public Wallet toDomain(){
        return Wallet.builder()
                .id(id)
                .walletName(walletName)
                .userName(userName)
                .balance(balance)
                .currency(currency)
                .createdAt(createdAt)
                .build();
    }
}
