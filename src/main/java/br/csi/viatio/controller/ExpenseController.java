package br.csi.viatio.controller;
import java.util.UUID;

import br.csi.viatio.model.Expense;
import br.csi.viatio.dto.expense.ExpenseRequest;
import br.csi.viatio.dto.expense.ExpenseResponse;
import br.csi.viatio.model.User;
import br.csi.viatio.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// criar, listar e deletar dados de uma viagem específica

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Despesas", description = "Endpoints para gerenciamento das despesas das viagens")
public class ExpenseController {

    private final ExpenseService expenseService;

    // ENDPOINT para registrar uma nova despesa em uma viagem
    @Operation(summary = "Registra uma nova despesa em uma viagem específica")
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest dados, @AuthenticationPrincipal User user) {
        // Aciona o service para registrar a despesa associada ao usuário logado
        Expense expense = expenseService.createExpense(dados, user);
        
        // Retorna os dados da despesa cadastrada formatados com status 200 OK
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    // ENDPOINT para buscar todas as despesas vinculadas a uma viagem específica
    @Operation(summary = "Lista todas as despesas de uma viagem")
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ExpenseResponse>> listByTrip(@PathVariable UUID tripId, @AuthenticationPrincipal User user) {
        // Carrega as despesas da viagem informada, validando se a viagem pertence ao usuário logado
        List<ExpenseResponse> expenses = expenseService.listByTrip(tripId, user)
                .stream()
                .map(ExpenseResponse::new)
                .toList();
                
        return ResponseEntity.ok(expenses);
    }

    // ENDPOINT para excluir uma despesa pelo seu identificador único UUID
    @Operation(summary = "Exclui uma despesa pelo seu identificador único UUID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        // Solicita ao serviço a remoção da despesa, garantindo que o usuário é o dono do registro
        expenseService.deleteExpense(id, user);
        
        // Retorna status 204 No Content para indicar sucesso na operação
        return ResponseEntity.noContent().build();
    }
}

