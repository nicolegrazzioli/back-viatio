package br.csi.pilago.controller;

import br.csi.pilago.infra.security.TokenServiceJWT;
import br.csi.pilago.model.user.LoginRequest;
import br.csi.pilago.model.user.RegisterRequest;
import br.csi.pilago.model.user.TokenResponse;
import br.csi.pilago.model.user.User;
import br.csi.pilago.model.user.UserRepository;
import br.csi.pilago.model.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenServiceJWT tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());
        Authentication authentication = manager.authenticate(authenticationToken);

        var usuario = (User) authentication.getPrincipal();
        String token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest dados) {
        if (userRepository.findByEmail(dados.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        User newUser = new User();
        newUser.setName(dados.name());
        newUser.setEmail(dados.email());
        newUser.setPassword(passwordEncoder.encode(dados.password()));
        newUser.setPhone(dados.phone());

        userRepository.save(newUser);

        return ResponseEntity.ok(new UserResponse(newUser));
    }
}
