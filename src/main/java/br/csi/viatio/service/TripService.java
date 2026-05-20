package br.csi.viatio.service;

import br.csi.viatio.model.trip.Trip;
import br.csi.viatio.model.trip.TripRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    private final TripRepository repository;

    public TripService(TripRepository repository) {
        this.repository = repository;
    }

    public List<Trip> findAll() {
        return repository.findAll();
    }

    public Optional<Trip> findById(Long id) {
        return repository.findById(id);
    }

    public Trip save(Trip trip) {
        return repository.save(trip);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

