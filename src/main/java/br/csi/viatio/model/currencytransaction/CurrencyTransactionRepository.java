package br.csi.viatio.model.currencytransaction;

import java.util.UUID;

import br.csi.viatio.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, UUID> {
    List<CurrencyTransaction> findByUser(User user);
}

