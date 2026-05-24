package br.csi.viatio.service;

import java.util.UUID;
import java.util.List;

import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.trip.Trip;
import br.csi.viatio.model.trip.TripRepository;
import br.csi.viatio.model.trip.TripRequest;
import br.csi.viatio.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    private final TripRepository repository;

    public TripService(TripRepository repository) {
        this.repository = repository;
    }

    public Trip createTrip(TripRequest dados, User user) {
        Trip trip;
        if (dados.id() != null) {
            trip = repository.findById(dados.id()).orElse(new Trip());
            trip.setId(dados.id());
        } else {
            trip = new Trip();
        }

        trip.setUser(user);
        trip.setTitle(dados.title());
        trip.setStartDate(dados.startDate());
        trip.setEndDate(dados.endDate());
        trip.setCoverType(dados.coverType());

        return repository.save(trip);
    }

    public List<Trip> listByUser(User user) {
        return repository.findByUser(user);
    }

    public void deleteTrip(UUID id, User user) {
        Trip trip = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));
        
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta viagem.");
        }
        
        repository.delete(trip);
    }
}
