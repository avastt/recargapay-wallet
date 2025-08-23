package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Transaction;
import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.enums.TransactionType;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class DepositAmount {

	private final WalletRepository walletRepository;
	private final TransactionRepository transactionRepository;
	private final GetUserWallet getUserWallet;

	@Transactional
	public Wallet execute(final UUID walletId, final BigDecimal amount) {
		var userWallet = getUserWallet.execute(walletId);
		updateWalletBalance(amount, userWallet);
		createTransaction(amount, userWallet);

		return userWallet;
	}

	private void updateWalletBalance(final BigDecimal amount, final Wallet wallet) {
		wallet.setBalance(wallet.getBalance().add(amount));
		walletRepository.save(new WalletModel(wallet)).toDomain();
	}

	private void createTransaction(final BigDecimal amount, final Wallet wallet) {
		Transaction transaction = Transaction.builder()
				.wallet(wallet)
				.amount(amount)
				.type(TransactionType.DEPOSIT)
				.historicalBalance(wallet.getBalance())
				.createdAt(LocalDateTime.now())
				.build();

		transactionRepository.save(new TransactionModel(transaction));
	}

}
