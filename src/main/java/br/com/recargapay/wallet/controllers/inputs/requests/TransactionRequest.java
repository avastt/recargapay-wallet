package br.com.recargapay.wallet.controllers.inputs.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
