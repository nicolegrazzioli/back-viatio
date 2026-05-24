package br.csi.viatio.service;

import br.csi.viatio.model.user.User;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.model.wallet.WalletId;
import br.csi.viatio.model.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public List<Wallet> listByUser(User user) {
        return repository.findByUser(user);
    }
}

