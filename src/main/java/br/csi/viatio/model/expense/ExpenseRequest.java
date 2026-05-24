package br.csi.viatio.model.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
    UUID id,
    
    @NotNull(message = "A viagem é obrigatória")
    UUID tripId,
    
    @NotBlank(message = "O título é obrigatório")
    String title,
    
    @NotNull(message = "O valor é obrigatório")
    BigDecimal amount,
    
    @NotBlank(message = "A moeda é obrigatória")
    String currency,
    
    @NotBlank(message = "A categoria é obrigatória")
    String category,
    
    @NotNull(message = "A data é obrigatória")
    LocalDate date,
    
    Boolean isAverageCost,
    BigDecimal exchangeRate,
    BigDecimal amountBrl,
    String description,
    String photoPath
) {}

