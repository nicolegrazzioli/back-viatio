package br.csi.viatio.controller;

import br.csi.viatio.model.User;
import br.csi.viatio.dto.wallet.WalletResponse;
import br.csi.viatio.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// Controlador REST responsável por exibir a carteira de moedas (saldos e VETs)
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Tag(name = "Carteiras (Wallets)", description = "Endpoints para consulta de saldos e VETs por moeda")
public class WalletController {

    private final WalletService walletService;

    // Endpoint para buscar todas as carteiras de moedas ativas (saldos e VETs médios) do usuário
    @Operation(summary = "Lista os saldos atuais consolidados de todas as moedas do usuário")
    @GetMapping
    public ResponseEntity<List<WalletResponse>> listAll(@AuthenticationPrincipal User user) {
        // Busca a carteira consolidada do usuário no banco e formata a resposta como DTO
        List<WalletResponse> wallets = walletService.listByUser(user)
                .stream()
                .map(WalletResponse::new)
                .toList();
                
        return ResponseEntity.ok(wallets);
    }
}


