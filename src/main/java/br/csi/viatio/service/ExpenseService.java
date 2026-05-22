package br.csi.viatio.service;

import java.util.UUID;

import br.csi.viatio.model.expense.Expense;
import br.csi.viatio.model.expense.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> findAll() {
        return repository.findAll();
    }

    public Optional<Expense> findById(UUID id) {
        return repository.findById(id);
    }

    public Expense save(Expense expense) {
        return repository.save(expense);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}

