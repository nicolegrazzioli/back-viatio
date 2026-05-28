package br.csi.viatio.controller;

import java.util.UUID;

import br.csi.viatio.model.Trip;
import br.csi.viatio.dto.trip.TripRequest;
import br.csi.viatio.dto.trip.TripResponse;
import br.csi.viatio.model.User;
import br.csi.viatio.service.TripService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

// Controlador REST para gerenciar as viagens cadastradas pelos usuários
@RestController
@RequestMapping("/trips")
@AllArgsConstructor
public class TripController {

    private final TripService tripService;

    // ENDPOINT para criar uma nova viagem
    @PostMapping
    public ResponseEntity<TripResponse> create(@Valid @RequestBody TripRequest dados, @AuthenticationPrincipal User user) {
        // Solicita ao serviço a criação do registro de viagem associada a este usuário
        Trip trip = tripService.createTrip(dados, user);
        
        // Retorna a viagem salva formatada como TripResponse com status 200 OK
        return ResponseEntity.ok(new TripResponse(trip));
    }

    // ENDPOINT para listar todas as viagens associadas ao usuário logado
    @GetMapping
    public ResponseEntity<List<TripResponse>> listAll(@AuthenticationPrincipal User user) {
        // Busca as viagens do usuário no banco e converte a lista em DTOs simplificados
        List<TripResponse> trips = tripService.listByUser(user)
                .stream()
                .map(TripResponse::new)
                .toList();
                
        return ResponseEntity.ok(trips);
    }

    // ENDPOINT para excluir uma viagem específica pelo seu identificador único UUID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        // Solicita a remoção da viagem, validando se ela realmente pertence ao usuário atual
        tripService.deleteTrip(id, user);
        
        // Retorna status 204 No Content (exclusão feita com sucesso e sem retorno de conteúdo no corpo)
        return ResponseEntity.noContent().build();
    }
}

