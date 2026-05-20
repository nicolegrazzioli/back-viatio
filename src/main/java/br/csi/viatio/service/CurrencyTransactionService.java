package br.csi.viatio.service;

import br.csi.viatio.model.currencytransaction.CurrencyTransaction;
import br.csi.viatio.model.currencytransaction.CurrencyTransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyTransactionService {

    private final CurrencyTransactionRepository repository;

    public CurrencyTransactionService(CurrencyTransactionRepository repository) {
        this.repository = repository;
    }

    public List<CurrencyTransaction> findAll() {
        return repository.findAll();
    }

    public Optional<CurrencyTransaction> findById(Long id) {
        return repository.findById(id);
    }

    public CurrencyTransaction save(CurrencyTransaction transaction) {
        return repository.save(transaction);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

