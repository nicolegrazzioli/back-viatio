package br.csi.viatio.model.expense;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
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
) {
    public ExpenseResponse(Expense expense) {
        this(
            expense.getId(),
            expense.getTrip().getId(),
            expense.getTitle(),
            expense.getAmount(),
            expense.getCurrency(),
            expense.getCategory(),
            expense.getDate(),
            expense.getIsAverageCost(),
            expense.getExchangeRate(),
            expense.getAmountBrl(),
            expense.getDescription(),
            expense.getPhotoPath()
        );
    }
}

