package br.csi.viatio.controller;

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

    @PostMapping
    public ResponseEntity<CurrencyTransactionResponse> create(@RequestBody CurrencyTransactionRequest dados) {
        User user = getAuthenticatedUser();

        CurrencyTransaction transaction = new CurrencyTransaction();
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

        // Atualizar Carteira (Wallet)
        WalletId walletId = new WalletId(user.getId(), dados.currency());
        Wallet wallet = walletRepository.findById(walletId).orElse(new Wallet(user, dados.currency(), BigDecimal.ZERO, BigDecimal.ZERO));

        BigDecimal totalBrlAntigo = wallet.getBalance().multiply(wallet.getAverageVet());
        BigDecimal novoSaldo = wallet.getBalance().add(dados.amount());
        BigDecimal novoTotalBrl = totalBrlAntigo.add(dados.amountBrl());

        if (novoSaldo.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setAverageVet(novoTotalBrl.divide(novoSaldo, 4, RoundingMode.HALF_UP));
        }
        wallet.setBalance(novoSaldo);

        walletRepository.save(wallet);

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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        CurrencyTransaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction == null || !transaction.getUser().getId().equals(getAuthenticatedUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        // Atualizar carteira (Subtrair)
        WalletId walletId = new WalletId(transaction.getUser().getId(), transaction.getCurrency());
        Wallet wallet = walletRepository.findById(walletId).orElse(null);
        
        if (wallet != null) {
            BigDecimal novoSaldo = wallet.getBalance().subtract(transaction.getAmount());
            if (novoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
                walletRepository.delete(wallet);
            } else {
                BigDecimal totalBrlAntigo = wallet.getBalance().multiply(wallet.getAverageVet());
                BigDecimal novoTotalBrl = totalBrlAntigo.subtract(transaction.getAmountBrl());
                wallet.setAverageVet(novoTotalBrl.divide(novoSaldo, 4, RoundingMode.HALF_UP));
                wallet.setBalance(novoSaldo);
                walletRepository.save(wallet);
            }
        }

        transactionRepository.delete(transaction);
        return ResponseEntity.noContent().build();
    }
}

