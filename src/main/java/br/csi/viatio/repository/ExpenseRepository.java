package br.csi.viatio.repository;

import java.util.UUID;

import br.csi.viatio.model.Expense;
import br.csi.viatio.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import br.csi.viatio.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByTrip(Trip trip);
    List<Expense> findByTrip_UserAndCurrency(User user, String currency);

    @Modifying
    @Transactional
    @Query("UPDATE Expense e SET e.exchangeRate = :newVet, e.amountBrl = e.amount * :newVet WHERE e.trip.id = :tripId AND e.currency = :currency AND e.isAverageCost = true")
    void updateTripExpensesVet(UUID tripId, String currency, BigDecimal newVet);
}
