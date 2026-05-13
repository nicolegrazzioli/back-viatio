package br.csi.pilago.controller;

import br.csi.pilago.model.trip.Trip;
import br.csi.pilago.model.trip.TripRepository;
import br.csi.pilago.model.trip.TripRequest;
import br.csi.pilago.model.trip.TripResponse;
import br.csi.pilago.model.user.User;
import br.csi.pilago.model.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@AllArgsConstructor
public class TripController {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<TripResponse> create(@RequestBody TripRequest dados) {
        User user = getAuthenticatedUser();

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTitle(dados.title());
        trip.setStartDate(dados.startDate());
        trip.setEndDate(dados.endDate());
        trip.setCoverType(dados.coverType());

        tripRepository.save(trip);
        return ResponseEntity.ok(new TripResponse(trip));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> listAll() {
        User user = getAuthenticatedUser();
        List<TripResponse> trips = tripRepository.findByUser(user)
                .stream()
                .map(TripResponse::new)
                .toList();
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Trip trip = tripRepository.findById(id).orElse(null);
        if (trip == null || !trip.getUser().getId().equals(getAuthenticatedUser().getId())) {
            return ResponseEntity.notFound().build();
        }
        tripRepository.delete(trip);
        return ResponseEntity.noContent().build();
    }
}
