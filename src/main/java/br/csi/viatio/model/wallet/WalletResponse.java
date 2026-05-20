package br.csi.viatio.model.wallet;

import java.math.BigDecimal;

public record WalletResponse(
    String currency,
    BigDecimal balance,
    BigDecimal averageVet
) {
    public WalletResponse(Wallet wallet) {
        this(wallet.getCurrency(), wallet.getBalance(), wallet.getAverageVet());
    }
}

