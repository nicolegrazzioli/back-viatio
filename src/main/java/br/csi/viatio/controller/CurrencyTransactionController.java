package br.csi.viatio.controller;
import java.util.UUID;
import java.util.List;

import br.csi.viatio.model.CurrencyTransaction;
import br.csi.viatio.dto.currencytransaction.CurrencyTransactionRequest;
import br.csi.viatio.dto.currencytransaction.CurrencyTransactionResponse;
import br.csi.viatio.model.User;
import br.csi.viatio.service.CurrencyTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

// recebe compra de moeda do celular e lista/remove do banco de dados

// Controlador REST para gerenciar as transações de compra de moedas estrangeiras
@RestController
@RequestMapping("/currency-transactions")
@RequiredArgsConstructor
public class CurrencyTransactionController {

    private final CurrencyTransactionService transactionService;

    // ENDPOINT para registrar uma compra de moeda estrangeira
    // ResponseEntity = resposta HTTP terá um corpo de dados e codigo de status
    // spring le o corpo json enviado pelo app flutter e converte no objeto CurrencyTransactionRequest dados
    @PostMapping
    public ResponseEntity<CurrencyTransactionResponse> create(@Valid @RequestBody CurrencyTransactionRequest dados, @AuthenticationPrincipal User user) {
        // Aciona o service para registrar a transação de compra associada ao usuário
        CurrencyTransaction transaction = transactionService.createTransaction(dados, user);
        
        // Retorna a transação criada formatada como DTO com status 200 OK para o flutter
        return ResponseEntity.ok(new CurrencyTransactionResponse(transaction));
    }

    // ENDPOINT para listar todas as transações de moedas do usuário logado
    @GetMapping
    public ResponseEntity<List<CurrencyTransactionResponse>> listAll(@AuthenticationPrincipal User user) {
        // Busca a lista de transações do usuário no banco e converte em DTOs formatados
        // .stream().map(CurrencyTransactionResponse::new) - pega a lista de entidades originais do banco e passa cada uma pelo construtor do DTO CurrencyTransactionResponse
        List<CurrencyTransactionResponse> transactions = transactionService.listByUser(user)
                .stream()
                .map(CurrencyTransactionResponse::new)
                .toList();
                
        return ResponseEntity.ok(transactions);
    }

    // ENDPOINT para remover uma compra de moeda específica pelo UUID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        // Solicita a exclusão do registro ao serviço, validando se ele pertence ao usuário
        transactionService.deleteTransaction(id, user);
        
        // Retorna 204 No Content se a exclusão for realizada com sucesso
        return ResponseEntity.noContent().build();
    }
}

