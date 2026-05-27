package br.csi.viatio.dto.currencytransaction;

import br.csi.viatio.model.CurrencyTransaction;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyTransactionResponse(
    UUID id,
    BigDecimal amount,
    String currency,
    BigDecimal amountBrl,
    String source,
    LocalDate date,
    BigDecimal vetRate,
    String photoPath
) {
    public CurrencyTransactionResponse(CurrencyTransaction transaction) {
        this(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getAmountBrl(),
            transaction.getSource(),
            transaction.getDate(),
            transaction.getVetRate(),
            transaction.getPhotoPath()
        );
    }
}
