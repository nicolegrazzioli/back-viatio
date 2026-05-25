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

    public CurrencyTransactionService(CurrencyTransactionRepository transactionRepository, WalletRepository walletRepository, ExpenseRepository expenseRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.expenseRepository = expenseRepository;
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

        // Se o VET mudou, aplica o recálculo dinâmico aos gastos da viagem que usam Custo Médio
        if (oldVet != null && newVet.compareTo(oldVet) != 0) {
            expenseRepository.updateDynamicVet(user, currency, newVet);
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
