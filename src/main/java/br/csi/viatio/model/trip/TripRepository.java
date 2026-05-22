package br.csi.viatio.model.trip;

import java.util.UUID;

import br.csi.viatio.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByUser(User user);
}

