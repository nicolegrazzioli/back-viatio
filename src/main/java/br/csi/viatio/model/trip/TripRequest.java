package br.csi.viatio.model.trip;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record TripRequest(
    UUID id,
    
    @NotBlank(message = "O título é obrigatório")
    String title,
    
    @NotNull(message = "A data de início é obrigatória")
    LocalDate startDate,
    
    LocalDate endDate,
    
    @NotBlank(message = "O tipo de capa é obrigatório")
    String coverType
) {}

