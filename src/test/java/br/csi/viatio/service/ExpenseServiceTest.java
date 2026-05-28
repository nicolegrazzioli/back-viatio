package br.csi.viatio.service;

import br.csi.viatio.dto.expense.ExpenseRequest;
import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.Expense;
import br.csi.viatio.model.Trip;
import br.csi.viatio.model.User;
import br.csi.viatio.repository.ExpenseRepository;
import br.csi.viatio.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private CurrencyTransactionService currencyTransactionService;

    @InjectMocks
    private ExpenseService service;

    private User user;
    private Trip trip;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setUser(user);
    }

    @Test
    void testCreateExpense_WhenTripNotFound_ShouldThrowNotFound() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest(null, UUID.randomUUID(), "Almoço", new BigDecimal("10.0"), "EUR", "Alimentação", LocalDate.of(2026, 10, 10), true, new BigDecimal("5.0"), new BigDecimal("50.0"), null);
        when(tripRepository.findById(request.tripId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.createExpense(request, user));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testCreateExpense_WhenUserIsNotOwner_ShouldThrowForbidden() {
        // Arrange
        User wrongUser = new User();
        wrongUser.setId(UUID.randomUUID());
        
        Trip otherTrip = new Trip();
        otherTrip.setId(UUID.randomUUID());
        otherTrip.setUser(wrongUser);

        ExpenseRequest request = new ExpenseRequest(null, otherTrip.getId(), "Almoço", new BigDecimal("10.0"), "EUR", "Alimentação", LocalDate.of(2026, 10, 10), true, new BigDecimal("5.0"), new BigDecimal("50.0"), null);
        when(tripRepository.findById(request.tripId())).thenReturn(Optional.of(otherTrip));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> service.createExpense(request, user));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void testCreateExpense_SuccessAndRecalculateWallet() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest(null, trip.getId(), "Almoço", new BigDecimal("10.0"), "EUR", "Alimentação", LocalDate.of(2026, 10, 10), true, new BigDecimal("5.0"), new BigDecimal("50.0"), null);
        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        
        Expense savedExpense = new Expense();
        savedExpense.setCurrency("EUR");
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        // Act
        service.createExpense(request, user);

        // Assert
        verify(expenseRepository, times(1)).save(any(Expense.class));
        verify(currencyTransactionService, times(1)).recalculateWallet(user, "EUR");
    }

    @Test
    void testDeleteExpense_WhenNotFound_ShouldThrowNotFound() {
        UUID expId = UUID.randomUUID();
        when(expenseRepository.findById(expId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteExpense(expId, user));
    }

    @Test
    void testDeleteExpense_WhenNotOwner_ShouldThrowForbidden() {
        UUID expId = UUID.randomUUID();
        Expense expense = new Expense();
        User wrongUser = new User();
        wrongUser.setId(UUID.randomUUID());
        Trip wrongTrip = new Trip();
        wrongTrip.setUser(wrongUser);
        expense.setTrip(wrongTrip);

        when(expenseRepository.findById(expId)).thenReturn(Optional.of(expense));

        assertThrows(ForbiddenException.class, () -> service.deleteExpense(expId, user));
    }
}
