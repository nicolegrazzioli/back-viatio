package br.csi.pilago.service;

import br.csi.pilago.model.wallet.Wallet;
import br.csi.pilago.model.wallet.WalletId;
import br.csi.pilago.model.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public List<Wallet> findAll() {
        return repository.findAll();
    }

    public Optional<Wallet> findById(WalletId id) {
        return repository.findById(id);
    }

    public Wallet save(Wallet wallet) {
        return repository.save(wallet);
    }

    public void delete(WalletId id) {
        repository.deleteById(id);
    }
}
