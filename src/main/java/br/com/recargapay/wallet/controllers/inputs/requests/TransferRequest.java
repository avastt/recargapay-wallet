package br.com.recargapay.wallet.controllers.inputs.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {

    private UUID fromWalletId;
    private UUID toWalletId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
