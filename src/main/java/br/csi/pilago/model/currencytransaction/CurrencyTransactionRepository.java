package br.csi.pilago.model.currencytransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, Long> {
}
