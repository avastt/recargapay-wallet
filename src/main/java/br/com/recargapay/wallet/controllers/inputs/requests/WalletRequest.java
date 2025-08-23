package br.com.recargapay.wallet.controllers.inputs.requests;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class WalletRequest {

	private String name;
	private String userId;
	private String currency;
	private BigDecimal balance;

/*	public Wallet toDomain(){
		return Wallet.builder()
				.name(this.name)
				.userId(this.userId)
				.balance(this.balance)
				.currency(this.currency)
				.build();
	} */
}
