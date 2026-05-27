package br.csi.viatio.infra;

import br.csi.viatio.model.User;
import br.csi.viatio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String testEmail = "a@a";
        if (userRepository.findByEmail(testEmail) == null) {
            User testUser = new User();
            testUser.setName("a");
            testUser.setEmail(testEmail);
            testUser.setPassword(passwordEncoder.encode("a"));
            userRepository.save(testUser);
            System.out.println("Usuário de testes 'a@a' injetado com sucesso!");
        } else {
            System.out.println("Usuário de testes 'a@a' já existe no banco.");
        }
    }
}
