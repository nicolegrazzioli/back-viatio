package br.csi.viatio.model.wallet;

import br.csi.viatio.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {
    List<Wallet> findByUser(User user);
}

