package br.csi.pilago.model.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
    Long tripId,
    String title,
    BigDecimal amount,
    String currency,
    String category,
    LocalDate date,
    Boolean isAverageCost,
    BigDecimal exchangeRate,
    BigDecimal amountBrl,
    String description,
    String photoPath
) {}
