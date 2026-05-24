package br.csi.viatio.controller;

import java.util.UUID;

import br.csi.viatio.model.expense.Expense;
import br.csi.viatio.model.expense.ExpenseRequest;
import br.csi.viatio.model.expense.ExpenseResponse;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import br.csi.viatio.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@AllArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest dados) {
        User user = getAuthenticatedUser();
        Expense expense = expenseService.createExpense(dados, user);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ExpenseResponse>> listByTrip(@PathVariable UUID tripId) {
        User user = getAuthenticatedUser();
        List<ExpenseResponse> expenses = expenseService.listByTrip(tripId, user)
                .stream()
                .map(ExpenseResponse::new)
                .toList();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        User user = getAuthenticatedUser();
        expenseService.deleteExpense(id, user);
        return ResponseEntity.noContent().build();
    }
}

