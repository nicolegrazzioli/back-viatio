package br.csi.viatio.controller;

import br.csi.viatio.model.User;
import br.csi.viatio.repository.UserRepository;
import br.csi.viatio.dto.wallet.WalletResponse;
import br.csi.viatio.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    // Mérodo auxiliar para obter as informações do usuário autenticado pela requisição JWT
    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    // Endpoint para buscar todas as carteiras de moedas ativas (saldos e VETs médios) do usuário
    @GetMapping
    public ResponseEntity<List<WalletResponse>> listAll() {
        // Obtém o usuário ativo logado na sessão
        User user = getAuthenticatedUser();
        
        // Busca a carteira consolidada do usuário no banco e formata a resposta como DTO
        List<WalletResponse> wallets = walletService.listByUser(user)
                .stream()
                .map(WalletResponse::new)
                .toList();
                
        return ResponseEntity.ok(wallets);
    }
}


