package br.csi.viatio.controller;

import java.util.UUID;

import br.csi.viatio.model.trip.Trip;
import br.csi.viatio.model.trip.TripRequest;
import br.csi.viatio.model.trip.TripResponse;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import br.csi.viatio.service.TripService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/trips")
@AllArgsConstructor
public class TripController {

    private final TripService tripService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<TripResponse> create(@Valid @RequestBody TripRequest dados) {
        User user = getAuthenticatedUser();
        Trip trip = tripService.createTrip(dados, user);
        return ResponseEntity.ok(new TripResponse(trip));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> listAll() {
        User user = getAuthenticatedUser();
        List<TripResponse> trips = tripService.listByUser(user)
                .stream()
                .map(TripResponse::new)
                .toList();
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        User user = getAuthenticatedUser();
        tripService.deleteTrip(id, user);
        return ResponseEntity.noContent().build();
    }
}

