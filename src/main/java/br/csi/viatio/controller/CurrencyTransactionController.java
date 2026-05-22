package br.csi.viatio.controller;

import java.util.UUID;

import br.csi.viatio.model.currencytransaction.CurrencyTransaction;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionRepository;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionRequest;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionResponse;
import br.csi.viatio.model.user.User;
import br.csi.viatio.model.user.UserRepository;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.model.wallet.WalletId;
import br.csi.viatio.model.wallet.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/currency-transactions")
@AllArgsConstructor
public class CurrencyTransactionController {

    private final CurrencyTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email);
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

        Wallet wallet = walletRepository.findById(walletId).orElse(new Wallet(user, currency, BigDecimal.ZERO, BigDecimal.ZERO));
        wallet.setBalance(totalBalance);
        
        if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setAverageVet(totalBrl.divide(totalBalance, 4, RoundingMode.HALF_UP));
        } else {
            wallet.setAverageVet(BigDecimal.ZERO);
        }
        
        walletRepository.save(wallet);
    }

    @PostMapping
    public ResponseEntity<CurrencyTransactionResponse> create(@RequestBody CurrencyTransactionRequest dados) {
        User user = getAuthenticatedUser();

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

        transactionRepository.save(transaction);

        // Recalcular Carteira (Wallet)
        recalculateWallet(user, dados.currency());

        return ResponseEntity.ok(new CurrencyTransactionResponse(transaction));
    }

    @GetMapping
    public ResponseEntity<List<CurrencyTransactionResponse>> listAll() {
        User user = getAuthenticatedUser();
        List<CurrencyTransactionResponse> transactions = transactionRepository.findByUser(user)
                .stream()
                .map(CurrencyTransactionResponse::new)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        CurrencyTransaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction == null || !transaction.getUser().getId().equals(getAuthenticatedUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        String currency = transaction.getCurrency();
        User user = transaction.getUser();
        
        transactionRepository.delete(transaction);
        
        // Recalcular carteira após deletar
        recalculateWallet(user, currency);
        
        return ResponseEntity.noContent().build();
    }
}

