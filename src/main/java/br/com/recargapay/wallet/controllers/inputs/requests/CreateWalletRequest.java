package br.com.recargapay.wallet.controllers.inputs.requests;

import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.exception.InvalidCurrencyTypeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Currency;

@Data
public class CreateWalletRequest {

	@NotBlank(message = "Wallet name cannot be empty")
	@Size(max = 50, message = "Wallet name must be at most 50 characters")
	private String walletName;

	@NotBlank(message = "User name cannot be empty")
	@Size(max = 50, message = "User name must be at most 50 characters")
	private String userName;

	@NotBlank(message = "Currency cannot be empty")
	@Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid ISO 4217 code (e.g., USD, BRL)")
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
