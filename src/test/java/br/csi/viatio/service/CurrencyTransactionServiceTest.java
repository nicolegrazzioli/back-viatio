package br.csi.viatio.service;

import br.csi.viatio.dto.currencytransaction.CurrencyTransactionRequest;
import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.CurrencyTransaction;
import br.csi.viatio.model.Expense;
import br.csi.viatio.model.Trip;
import br.csi.viatio.model.User;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.model.wallet.WalletId;
import br.csi.viatio.repository.CurrencyTransactionRepository;
import br.csi.viatio.repository.ExpenseRepository;
import br.csi.viatio.repository.TripRepository;
import br.csi.viatio.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyTransactionServiceTest {

    @Mock
    private CurrencyTransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private CurrencyTransactionService service;

    private User user;
    private String currency;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        currency = "EUR";
    }

    @Test
    void testRecalculateWallet_WithEmptyTransactions_ShouldDeleteWallet() {
        // Arrange
        WalletId walletId = new WalletId(user.getId(), currency);
        Wallet existingWallet = new Wallet();
        when(transactionRepository.findByUserAndCurrency(user, currency)).thenReturn(new ArrayList<>());
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));

        // Act
        service.recalculateWallet(user, currency);

        // Assert
        verify(walletRepository, times(1)).delete(existingWallet);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testRecalculateWallet_WithTransactions_ShouldUpdateBalanceAndVet() {
        // Arrange
        CurrencyTransaction tx1 = new CurrencyTransaction();
        tx1.setAmount(new BigDecimal("100.00")); // comprou 100 EUR
        tx1.setAmountBrl(new BigDecimal("500.00")); // custou 500 BRL
        tx1.setDate(LocalDate.now());

        CurrencyTransaction tx2 = new CurrencyTransaction();
        tx2.setAmount(new BigDecimal("50.00")); // comprou 50 EUR
        tx2.setAmountBrl(new BigDecimal("300.00")); // custou 300 BRL
        tx2.setDate(LocalDate.now());

        when(transactionRepository.findByUserAndCurrency(user, currency)).thenReturn(List.of(tx1, tx2));

        Expense exp1 = new Expense();
        exp1.setAmount(new BigDecimal("40.00")); // gastou 40 EUR
        when(expenseRepository.findByTrip_UserAndCurrency(user, currency)).thenReturn(List.of(exp1));

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setEndDate(LocalDate.now().plusDays(5));
        when(tripRepository.findByUser(user)).thenReturn(List.of(trip));

        WalletId walletId = new WalletId(user.getId(), currency);
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty()); // Wallet nova

        // Act
        service.recalculateWallet(user, currency);

        // Assert
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(1)).save(walletCaptor.capture());
        
        Wallet savedWallet = walletCaptor.getValue();
        // Total comprado: 150. Gasto: 40. Restante: 110
        assertEquals(new BigDecimal("110.00"), savedWallet.getBalance());
        // Total BRL pago: 800. Moedas compradas: 150. VET: 800 / 150 = 5.3333
        assertEquals(new BigDecimal("5.3333"), savedWallet.getAverageVet());
        
        // Assert do Cascade
        verify(expenseRepository, times(1)).updateTripExpensesVet(eq(trip.getId()), eq(currency), eq(new BigDecimal("5.3333")));
    }

    @Test
    void testDeleteTransaction_WhenUserIsNotOwner_ShouldThrowForbidden() {
        // Arrange
        UUID txId = UUID.randomUUID();
        CurrencyTransaction tx = new CurrencyTransaction();
        User wrongUser = new User();
        wrongUser.setId(UUID.randomUUID());
        tx.setUser(wrongUser);

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> service.deleteTransaction(txId, user));
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void testDeleteTransaction_WhenTransactionNotFound_ShouldThrowNotFound() {
        // Arrange
        UUID txId = UUID.randomUUID();
        when(transactionRepository.findById(txId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.deleteTransaction(txId, user));
    }

    @Test
    void testDeleteTransaction_Success() {
        // Arrange
        UUID txId = UUID.randomUUID();
        CurrencyTransaction tx = new CurrencyTransaction();
        tx.setUser(user);
        tx.setCurrency(currency);

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));
        when(transactionRepository.findByUserAndCurrency(user, currency)).thenReturn(new ArrayList<>()); // mock para recalcular

        // Act
        service.deleteTransaction(txId, user);

        // Assert
        verify(transactionRepository, times(1)).delete(tx);
        // O recálculo vai chamar walletRepository.findById
        verify(walletRepository, times(1)).findById(any());
    }
}
