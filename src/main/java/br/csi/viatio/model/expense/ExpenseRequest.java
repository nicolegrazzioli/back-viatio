package br.csi.viatio.model.expense;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
    UUID id,
    UUID tripId,
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

