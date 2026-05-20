package br.csi.viatio.model.currencytransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyTransactionRequest(
    BigDecimal amount,
    String currency,
    BigDecimal amountBrl,
    String source,
    LocalDate date,
    BigDecimal vetRate,
    String description,
    String photoPath
) {}

