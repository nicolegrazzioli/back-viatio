package br.csi.viatio.controller;

import br.csi.viatio.model.User;
import br.csi.viatio.dto.wallet.WalletResponse;
import br.csi.viatio.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Controlador REST responsável por exibir a carteira de moedas (saldos e VETs)
@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Endpoint para buscar todas as carteiras de moedas ativas (saldos e VETs médios) do usuário
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


