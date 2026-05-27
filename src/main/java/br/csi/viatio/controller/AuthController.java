package br.csi.viatio.controller;
import br.csi.viatio.infra.security.TokenServiceJWT;
import br.csi.viatio.dto.user.LoginRequest;
import br.csi.viatio.dto.user.RegisterRequest;
import br.csi.viatio.dto.user.TokenResponse;
import br.csi.viatio.model.User;
import br.csi.viatio.repository.UserRepository;
import br.csi.viatio.dto.user.UserResponse;
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

// login e cadastro

// Define que a classe lida com requisições HTTP REST e retorna os dados diretamente em formato JSON
@RestController
// Mapeia todas as rotas desta classe para iniciar com /auth
@RequestMapping("/auth")
// Lombok gera automaticamente o construtor com os campos finais (injeção de dependência)
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenServiceJWT tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ENDPOINT de login do usuário: valida as credenciais e retorna o token de acesso
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest dados) { // Recebe dados do DTO
        // Envelopa o email e senha recebidos no corpo da requisição em um token do Spring Security
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());
        
        // Gerenciador Spring verifica se o usuário e a senha são válidos no banco
        Authentication authentication = manager.authenticate(authenticationToken);

        // Faz a conversão para a classe User
        var usuario = (User) authentication.getPrincipal();

        // Cria o token JWT criptografado (bcrypt)
        String token = tokenService.gerarToken(usuario);

        // Retorna o token gerado e os dados públicos do usuário autenticado com status 200 OK
        return ResponseEntity.ok(new TokenResponse(token, new UserResponse(usuario)));
    }

    // ENDPOINT de cadastro de novos usuários
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest dados) {
        // Verifica no banco de dados se o e-mail informado já está cadastrado
        if (userRepository.findByEmail(dados.email()) != null) {
            // Se já existir cadastro com o e-mail, retorna erro 400 (Bad Request)
            return ResponseEntity.badRequest().build();
        }

        // Instancia a nova entidade de usuário com os dados do DTO recebido
        User newUser = new User();
        newUser.setName(dados.name());
        newUser.setEmail(dados.email());
        
        // Criptografa a senha antes de salvar no banco
        newUser.setPassword(passwordEncoder.encode(dados.password()));

        // Salva o novo usuário na tabela correspondente do banco PostgreSQL
        userRepository.save(newUser);

        // Retorna os dados simplificados do usuário cadastrado (sem a senha)
        return ResponseEntity.ok(new UserResponse(newUser));
    }
}

