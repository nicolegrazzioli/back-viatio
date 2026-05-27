package br.csi.viatio.repository;

import br.csi.viatio.model.User;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.model.wallet.WalletId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {
    List<Wallet> findByUser(User user);
}

