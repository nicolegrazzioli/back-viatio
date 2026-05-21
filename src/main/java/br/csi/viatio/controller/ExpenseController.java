package br.csi.viatio.controller;

import br.csi.viatio.model.expense.Expense;
import br.csi.viatio.model.expense.ExpenseRepository;
import br.csi.viatio.model.expense.ExpenseRequest;
import br.csi.viatio.model.expense.ExpenseResponse;
import br.csi.viatio.model.trip.Trip;
import br.csi.viatio.model.trip.TripRepository;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@AllArgsConstructor
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@RequestBody ExpenseRequest dados) {
        Trip trip = tripRepository.findById(dados.tripId()).orElse(null);
        if (trip == null || !trip.getUser().getId().equals(getAuthenticatedUser().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Expense expense = new Expense();
        expense.setTrip(trip);
        expense.setTitle(dados.title());
        expense.setAmount(dados.amount());
        expense.setCurrency(dados.currency());
        expense.setCategory(dados.category());
        expense.setDate(dados.date());
        expense.setIsAverageCost(dados.isAverageCost());
        expense.setExchangeRate(dados.exchangeRate());
        expense.setAmountBrl(dados.amountBrl());
        expense.setDescription(dados.description());
        expense.setPhotoPath(dados.photoPath());

        expenseRepository.save(expense);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ExpenseResponse>> listByTrip(@PathVariable Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null || !trip.getUser().getId().equals(getAuthenticatedUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        List<ExpenseResponse> expenses = expenseRepository.findByTrip(trip)
                .stream()
                .map(ExpenseResponse::new)
                .toList();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense == null || !expense.getTrip().getUser().getId().equals(getAuthenticatedUser().getId())) {
            return ResponseEntity.notFound().build();
        }
        expenseRepository.delete(expense);
        return ResponseEntity.noContent().build();
    }
}

