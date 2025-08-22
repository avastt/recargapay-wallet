package br.com.recargapay.wallet.exception;

public class InvalidCurrencyTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidCurrencyTypeException(String msg) {
		super(msg);
	}
}
