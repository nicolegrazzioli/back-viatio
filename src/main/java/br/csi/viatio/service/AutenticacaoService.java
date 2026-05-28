package br.csi.viatio.service;
import br.csi.viatio.model.User;
import br.csi.viatio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Classe de serviço do Spring Security utilizada para carregar dados do usuário durante a autenticação
@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {
    private final UserRepository repository;

    // Mérodo para validar o login do usuário
    // recebe o e-mail inserido na autenticação e faz a busca no banco de dados
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário na tabela correspondente pelo UserRepository
        User user = repository.findByEmail(email);

        // Se o usuário não existir no banco, lança erro de credenciais inválidas para o Spring Security
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
        }
        
        // Retorna a entidade de usuário (que implementa UserDetails) para o Spring Security validar a senha
        return user;
    }
}

