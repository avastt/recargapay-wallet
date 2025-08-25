package br.com.recargapay.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorKey {

    USER_WALLET_NOT_FOUND("user.wallet.not.found"),
    TRANSACTION_NOT_FOUND("transaction.not.found"),
    INSUFFICIENT_FUNDS("insufficient.funds"),
    OPTIMISTIC_LOCKING("optimistic.locking.failure"),
    INVALID_CURRENCY_TYPE("invalid.currency.type");

    private final String key;
}
