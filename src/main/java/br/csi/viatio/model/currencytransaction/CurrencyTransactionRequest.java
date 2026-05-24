package br.csi.viatio.model.currencytransaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CurrencyTransactionRequest(
    UUID id,
    
    @NotNull(message = "O valor é obrigatório")
    BigDecimal amount,
    
    @NotBlank(message = "A moeda é obrigatória")
    String currency,
    
    @NotNull(message = "O valor convertido em BRL é obrigatório")
    BigDecimal amountBrl,
    
    @NotBlank(message = "A origem é obrigatória")
    String source,
    
    @NotNull(message = "A data é obrigatória")
    LocalDate date,
    
    @NotNull(message = "A taxa VET é obrigatória")
    BigDecimal vetRate,
    
    String description,
    String photoPath
) {}

