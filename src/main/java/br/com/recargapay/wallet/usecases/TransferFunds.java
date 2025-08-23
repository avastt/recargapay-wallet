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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.recargapay.wallet.enums.TransactionType.*;
import static br.com.recargapay.wallet.exception.ErrorKey.INSUFFICIENT_FUNDS;


@Component
@RequiredArgsConstructor
public class TransferFunds {

	private final WalletRepository walletRepository;
	private final TransactionRepository transactionRepository;
	private final GetUserWallet getUserWallet;
	private final MessageUtils messageUtils;

	@Transactional
	public Wallet execute(final UUID fromWalletId, final UUID toWalletId, final BigDecimal amount ) {

		var fromWallet = getUserWallet.execute(fromWalletId);
		var toWallet = getUserWallet.execute(toWalletId);

		if(fromWallet.getBalance().compareTo(amount) < 0){
			throw new InsufficientFundsException(messageUtils.getMessage(INSUFFICIENT_FUNDS));
		}

		updateWalletBalances(amount, fromWallet, toWallet);
		createTransferTransactions(amount, fromWallet, toWallet);

		return fromWallet;
	}

	private void createTransferTransactions(final BigDecimal amount, final Wallet fromWallet, final Wallet toWallet) {
		transactionRepository.save(new TransactionModel(buildTransaction(amount, fromWallet, TRANSFER_OUT)));
		transactionRepository.save(new TransactionModel(buildTransaction(amount, toWallet, TRANSFER_IN)));
	}

	private void updateWalletBalances(final BigDecimal amount, final Wallet fromWallet, final Wallet toWallet) {
		fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
		toWallet.setBalance(toWallet.getBalance().add(amount));

		walletRepository.save(new WalletModel(fromWallet)).toDomain();
		walletRepository.save(new WalletModel(toWallet)).toDomain();
	}

	private static Transaction buildTransaction(final BigDecimal amount, final Wallet wallet,
												final TransactionType type) {
		return Transaction.builder()
				.wallet(wallet)
				.amount(amount)
				.type(type)
				.historicalBalance(wallet.getBalance())
				.createdAt(LocalDateTime.now())
				.build();
	}

}
