package br.csi.viatio.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.List;

import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.currencytransaction.CurrencyTransaction;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionRepository;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionRequest;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.model.wallet.WalletId;
import br.csi.viatio.model.wallet.WalletRepository;
import br.csi.viatio.model.expense.Expense;
import br.csi.viatio.model.expense.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class CurrencyTransactionService {

    private final CurrencyTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final ExpenseRepository expenseRepository;
    private final br.csi.viatio.model.trip.TripRepository tripRepository;

    public CurrencyTransactionService(CurrencyTransactionRepository transactionRepository, WalletRepository walletRepository, ExpenseRepository expenseRepository, br.csi.viatio.model.trip.TripRepository tripRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
    }

    public void recalculateWallet(User user, String currency) {
        List<CurrencyTransaction> txs = transactionRepository.findByUserAndCurrency(user, currency);
        WalletId walletId = new WalletId(user.getId(), currency);
        
        if (txs.isEmpty()) {
            walletRepository.findById(walletId).ifPresent(walletRepository::delete);
            return;
        }

        BigDecimal totalBought = BigDecimal.ZERO;
        BigDecimal totalBrl = BigDecimal.ZERO;

        for (CurrencyTransaction tx : txs) {
            totalBought = totalBought.add(tx.getAmount());
            totalBrl = totalBrl.add(tx.getAmountBrl());
        }

        // Subtrai os gastos pagos com a carteira (agora simplificado, todo gasto na moeda da carteira desconta)
        List<Expense> expenses = expenseRepository.findByTrip_UserAndCurrency(user, currency);
        BigDecimal totalSpent = BigDecimal.ZERO;
        for (Expense exp : expenses) {
            totalSpent = totalSpent.add(exp.getAmount());
        }

        BigDecimal remainingBalance = totalBought.subtract(totalSpent);

        Wallet wallet = walletRepository.findById(walletId).orElseGet(() -> {
            Wallet w = new Wallet();
            w.setUser(user);
            w.setCurrency(currency);
            w.setBalance(BigDecimal.ZERO);
            w.setAverageVet(BigDecimal.ZERO);
            return w;
        });
        
        BigDecimal oldVet = wallet.getAverageVet();
        BigDecimal newVet = BigDecimal.ZERO;
        
        wallet.setBalance(remainingBalance);
        
        if (totalBought.compareTo(BigDecimal.ZERO) > 0) {
            newVet = totalBrl.divide(totalBought, 4, RoundingMode.HALF_UP);
        }
        
        wallet.setAverageVet(newVet);
        walletRepository.save(wallet);

        // Se o VET mudou (ou mesmo se não mudou globalmente, mas as transações mudaram), aplica o recálculo dinâmico por viagem
        List<br.csi.viatio.model.trip.Trip> trips = tripRepository.findByUser(user);
        for (br.csi.viatio.model.trip.Trip trip : trips) {
            java.time.LocalDate cutoffDate = trip.getEndDate();
            
            BigDecimal tripBought = BigDecimal.ZERO;
            BigDecimal tripBrl = BigDecimal.ZERO;
            
            for (CurrencyTransaction tx : txs) {
                if (cutoffDate == null || !tx.getDate().isAfter(cutoffDate)) {
                    tripBought = tripBought.add(tx.getAmount());
                    tripBrl = tripBrl.add(tx.getAmountBrl());
                }
            }
            
            BigDecimal tripVet = BigDecimal.ZERO;
            if (tripBought.compareTo(BigDecimal.ZERO) > 0) {
                tripVet = tripBrl.divide(tripBought, 4, RoundingMode.HALF_UP);
            } else {
                tripVet = BigDecimal.ONE;
            }
            
            if (tripVet.compareTo(BigDecimal.ZERO) > 0) {
                expenseRepository.updateTripExpensesVet(trip.getId(), currency, tripVet);
            }
        }
    }

    public CurrencyTransaction createTransaction(CurrencyTransactionRequest dados, User user) {
        CurrencyTransaction transaction;
        if (dados.id() != null) {
            transaction = transactionRepository.findById(dados.id()).orElse(new CurrencyTransaction());
            transaction.setId(dados.id());
        } else {
            transaction = new CurrencyTransaction();
        }
        
        transaction.setUser(user);
        transaction.setAmount(dados.amount());
        transaction.setCurrency(dados.currency());
        transaction.setAmountBrl(dados.amountBrl());
        transaction.setSource(dados.source());
        transaction.setDate(dados.date());
        transaction.setVetRate(dados.vetRate());
        transaction.setDescription(dados.description());
        transaction.setPhotoPath(dados.photoPath());

        transaction = transactionRepository.save(transaction);
        recalculateWallet(user, dados.currency());
        return transaction;
    }

    public List<CurrencyTransaction> listByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public void deleteTransaction(UUID id, User user) {
        CurrencyTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta transação.");
        }

        String currency = transaction.getCurrency();
        transactionRepository.delete(transaction);
        recalculateWallet(user, currency);
    }
}
