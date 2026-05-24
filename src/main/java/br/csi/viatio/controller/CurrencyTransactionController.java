package br.csi.viatio.controller;

import java.util.UUID;
import java.util.List;

import br.csi.viatio.model.currencytransaction.CurrencyTransaction;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionRequest;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionResponse;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import br.csi.viatio.service.CurrencyTransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/currency-transactions")
@AllArgsConstructor
public class CurrencyTransactionController {

    private final CurrencyTransactionService transactionService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<CurrencyTransactionResponse> create(@Valid @RequestBody CurrencyTransactionRequest dados) {
        User user = getAuthenticatedUser();
        CurrencyTransaction transaction = transactionService.createTransaction(dados, user);
        return ResponseEntity.ok(new CurrencyTransactionResponse(transaction));
    }

    @GetMapping
    public ResponseEntity<List<CurrencyTransactionResponse>> listAll() {
        User user = getAuthenticatedUser();
        List<CurrencyTransactionResponse> transactions = transactionService.listByUser(user)
                .stream()
                .map(CurrencyTransactionResponse::new)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        User user = getAuthenticatedUser();
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.noContent().build();
    }
}

