package br.csi.pilago.model.currencytransaction;

import br.csi.pilago.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, Long> {
    List<CurrencyTransaction> findByUser(User user);
}
