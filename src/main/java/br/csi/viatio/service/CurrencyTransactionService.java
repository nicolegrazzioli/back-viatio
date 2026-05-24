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
import org.springframework.stereotype.Service;

@Service
public class CurrencyTransactionService {

    private final CurrencyTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public CurrencyTransactionService(CurrencyTransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    private void recalculateWallet(User user, String currency) {
        List<CurrencyTransaction> txs = transactionRepository.findByUserAndCurrency(user, currency);
        WalletId walletId = new WalletId(user.getId(), currency);
        
        if (txs.isEmpty()) {
            walletRepository.findById(walletId).ifPresent(walletRepository::delete);
            return;
        }

        BigDecimal totalBalance = BigDecimal.ZERO;
        BigDecimal totalBrl = BigDecimal.ZERO;

        for (CurrencyTransaction tx : txs) {
            totalBalance = totalBalance.add(tx.getAmount());
            totalBrl = totalBrl.add(tx.getAmountBrl());
        }

        Wallet wallet = walletRepository.findById(walletId).orElseGet(() -> {
            Wallet w = new Wallet();
            w.setUser(user);
            w.setCurrency(currency);
            w.setBalance(BigDecimal.ZERO);
            w.setAverageVet(BigDecimal.ZERO);
            return w;
        });
        wallet.setBalance(totalBalance);
        
        if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setAverageVet(totalBrl.divide(totalBalance, 4, RoundingMode.HALF_UP));
        } else {
            wallet.setAverageVet(BigDecimal.ZERO);
        }
        
        walletRepository.save(wallet);
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
