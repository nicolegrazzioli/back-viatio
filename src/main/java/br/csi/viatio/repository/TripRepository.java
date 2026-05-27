package br.csi.viatio.repository;

import java.util.UUID;

import br.csi.viatio.model.Trip;
import br.csi.viatio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByUser(User user);
}

