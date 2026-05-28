package br.csi.viatio.service;

import java.util.UUID;

import br.csi.viatio.model.User;
import br.csi.viatio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

// Classe de serviço responsável pelas operações de CRUD de usuários
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // Busca e retorna uma lista com todos os usuários cadastrados
    public List<User> findAll() {
        return repository.findAll();
    }

    // Busca um usuário a partir do seu identificador único UUID
    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    // Salva ou atualiza os dados de um usuário no banco de dados
    public User save(User user) {
        return repository.save(user);
    }

    // Exclui um registro de usuário do banco a partir do seu UUID
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
