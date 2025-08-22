package br.com.recargapay.wallet.controllers.inputs.requests;

import br.com.recargapay.wallet.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
public class CreateTransactionRequest {

	private BigDecimal amount;
	private TransactionType type;
	private LocalDateTime minDate;
	private LocalDateTime maxDate;

	private Currency convert(final String code) {
		try {
			return Currency.getInstance(code);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(code);
		}
	}
}
