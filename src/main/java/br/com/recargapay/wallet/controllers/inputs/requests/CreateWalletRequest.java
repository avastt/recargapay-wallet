package br.com.recargapay.wallet.controllers.inputs.requests;

import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.exception.InvalidCurrencyTypeException;
import lombok.Data;

import java.util.Currency;

@Data
public class CreateWalletRequest {

	private String walletName;
	private String userName;
	private String currency;

	public Wallet toDomain(){
		return Wallet.builder()
				.walletName(this.walletName)
				.userName(this.userName)
				.currency(convert(this.currency))
				.build();
	}

	private Currency convert(final String code) {
		try {
			return Currency.getInstance(code);
		} catch (IllegalArgumentException ex) {
			throw new InvalidCurrencyTypeException(code);
		}
	}
}
