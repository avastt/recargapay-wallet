package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.models.WalletModel;
import br.com.recargapay.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class CreateUserWallet {

	private WalletRepository walletRepository;

	@Transactional
	public Wallet execute(final Wallet wallet) {
		return walletRepository.save(new WalletModel(wallet)).toDomain();
	}

}
