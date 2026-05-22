package br.csi.viatio.model.trip;

import java.time.LocalDate;

import java.util.UUID;

public record TripRequest(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String coverType
) {}

