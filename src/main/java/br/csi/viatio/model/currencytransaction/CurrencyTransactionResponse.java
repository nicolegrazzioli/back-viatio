package br.csi.viatio.model.currencytransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyTransactionResponse(
    Long id,
    BigDecimal amount,
    String currency,
    BigDecimal amountBrl,
    String source,
    LocalDate date,
    BigDecimal vetRate,
    String description,
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
            transaction.getDescription(),
            transaction.getPhotoPath()
        );
    }
}

