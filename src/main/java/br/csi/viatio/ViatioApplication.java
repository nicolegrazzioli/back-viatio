package br.csi.viatio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;

@SpringBootApplication
public class ViatioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViatioApplication.class, args);
    }

    @Bean
    CommandLineRunner initUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("a@a") == null) {
                User user = new User();
                user.setName("a");
                user.setEmail("a@a");
                user.setPassword(passwordEncoder.encode("a"));
                userRepository.save(user);
                System.out.println("TEST USER CREATED: a@a / a");
            }
        };
    }
}

