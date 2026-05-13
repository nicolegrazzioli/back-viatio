package br.csi.pilago.model.trip;

import java.time.LocalDate;

public record TripResponse(
    Long id,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String coverType
) {
    public TripResponse(Trip trip) {
        this(trip.getId(), trip.getTitle(), trip.getStartDate(), trip.getEndDate(), trip.getCoverType());
    }
}
