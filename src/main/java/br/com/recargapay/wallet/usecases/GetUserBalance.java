package br.com.recargapay.wallet.usecases;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetUserBalance {

	private final GetUserWallet getUserWallet;

	public BigDecimal execute(final UUID id) {
		var wallet = getUserWallet.execute(id);
		return wallet.getBalance();
	}

}
