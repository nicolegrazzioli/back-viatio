package br.csi.pilago.model.trip;

import java.time.LocalDate;

public record TripRequest(
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String coverType
) {}
