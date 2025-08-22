package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Transaction;
import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.enums.TransactionType;
import br.com.recargapay.wallet.exception.InsufficientFundsException;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.repository.WalletRepository;
import br.com.recargapay.wallet.utils.MessageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.recargapay.wallet.exception.ErrorKey.INSUFFICIENT_FUNDS;

@Component
@RequiredArgsConstructor
public class WithdrawAmount {

    private GetUserWallet getUserWallet;

    private WalletRepository walletRepository;

    private TransactionRepository transactionRepository;

    private MessageUtils messageUtils;

    @Transactional
    public Wallet execute(final UUID walletId, final BigDecimal amount){
        var userWallet = getUserWallet.execute(walletId);
        updateWalletBalance(amount, userWallet);
        createTransaction(amount, userWallet);

        return userWallet;
    }

    private void updateWalletBalance(final BigDecimal amount, final Wallet wallet) {
        if(wallet.getBalance().compareTo(amount) < 0){
            throw new InsufficientFundsException(messageUtils.getMessage(INSUFFICIENT_FUNDS));
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(new WalletModel(wallet)).toDomain();
    }

    private void createTransaction(final BigDecimal amount, final Wallet wallet) {
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .historicalBalance(wallet.getBalance())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(new TransactionModel(transaction));
    }
}
