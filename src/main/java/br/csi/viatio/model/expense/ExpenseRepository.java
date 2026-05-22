package br.csi.viatio.model.expense;

import java.util.UUID;

import br.csi.viatio.model.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByTrip(Trip trip);
}

