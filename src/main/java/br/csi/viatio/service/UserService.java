package br.csi.viatio.service;

import java.util.UUID;

import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}

