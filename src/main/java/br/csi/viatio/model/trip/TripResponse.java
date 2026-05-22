package br.csi.viatio.model.trip;

import java.util.UUID;

import java.time.LocalDate;

public record TripResponse(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String coverType
) {
    public TripResponse(Trip trip) {
        this(trip.getId(), trip.getTitle(), trip.getStartDate(), trip.getEndDate(), trip.getCoverType());
    }
}

