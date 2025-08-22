package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.exception.NotFoundException;
import br.com.recargapay.wallet.repository.WalletRepository;
import br.com.recargapay.wallet.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static br.com.recargapay.wallet.exception.ErrorKey.USER_WALLET_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class GetUserWallet {

	private WalletRepository walletRepository;

	private MessageUtils messageUtils;

	public Wallet execute(final UUID id) {
		return walletRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(messageUtils.getMessage(USER_WALLET_NOT_FOUND, id)))
				.toDomain();
	}

}
