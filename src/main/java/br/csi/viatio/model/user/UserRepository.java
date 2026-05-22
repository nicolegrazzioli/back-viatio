package br.csi.viatio.model.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
}

