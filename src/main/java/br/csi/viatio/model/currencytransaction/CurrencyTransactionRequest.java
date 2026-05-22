package br.csi.viatio.model.currencytransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.UUID;

public record CurrencyTransactionRequest(
    UUID id,
    BigDecimal amount,
    String currency,
    BigDecimal amountBrl,
    String source,
    LocalDate date,
    BigDecimal vetRate,
    String description,
    String photoPath
) {}

