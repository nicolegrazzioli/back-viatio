package br.csi.viatio.repository;

import java.util.UUID;

import br.csi.viatio.model.CurrencyTransaction;
import br.csi.viatio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, UUID> {
    List<CurrencyTransaction> findByUser(User user);
    List<CurrencyTransaction> findByUserAndCurrency(User user, String currency);
}
