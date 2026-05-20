package br.csi.viatio.controller;

import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import br.csi.viatio.model.wallet.WalletRepository;
import br.csi.viatio.model.wallet.WalletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
public class WalletController {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
    }

    @GetMapping
    public ResponseEntity<List<WalletResponse>> listAll() {
        User user = getAuthenticatedUser();
        List<WalletResponse> wallets = walletRepository.findByUser(user)
                .stream()
                .map(WalletResponse::new)
                .toList();
        return ResponseEntity.ok(wallets);
    }
}

