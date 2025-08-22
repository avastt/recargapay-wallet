package br.com.recargapay.wallet.controllers.inputs.responses;

import br.com.recargapay.wallet.domains.Wallet;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WalletResponse {

    private UUID id;
    private String walletName;
    private String userName;

    public static WalletResponse createResponse(final Wallet wallet){
        return WalletResponse.builder()
                .id(wallet.getId())
                .walletName(wallet.getWalletName())
                .userName(wallet.getUserName())
                .build();
    }

}
