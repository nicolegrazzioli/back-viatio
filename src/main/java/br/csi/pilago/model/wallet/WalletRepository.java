package br.csi.pilago.model.wallet;

import br.csi.pilago.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletId> {
    List<Wallet> findByUser(User user);
}
