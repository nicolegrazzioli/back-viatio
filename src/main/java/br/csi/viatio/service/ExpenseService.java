package br.csi.viatio.service;

import java.util.UUID;
import java.util.List;

import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.expense.Expense;
import br.csi.viatio.model.expense.ExpenseRepository;
import br.csi.viatio.model.expense.ExpenseRequest;
import br.csi.viatio.model.trip.Trip;
import br.csi.viatio.model.trip.TripRepository;
import br.csi.viatio.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;

    public ExpenseService(ExpenseRepository expenseRepository, TripRepository tripRepository) {
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
    }

    public Expense createExpense(ExpenseRequest dados, User user) {
        Trip trip = tripRepository.findById(dados.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para adicionar despesas nesta viagem.");
        }

        Expense expense;
        if (dados.id() != null) {
            expense = expenseRepository.findById(dados.id()).orElse(new Expense());
            expense.setId(dados.id());
        } else {
            expense = new Expense();
        }

        expense.setTrip(trip);
        expense.setTitle(dados.title());
        expense.setAmount(dados.amount());
        expense.setCurrency(dados.currency());
        expense.setCategory(dados.category());
        expense.setDate(dados.date());
        expense.setIsAverageCost(dados.isAverageCost());
        expense.setExchangeRate(dados.exchangeRate());
        expense.setAmountBrl(dados.amountBrl());
        expense.setDescription(dados.description());
        expense.setPhotoPath(dados.photoPath());

        return expenseRepository.save(expense);
    }

    public List<Expense> listByTrip(UUID tripId, User user) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para ver as despesas desta viagem.");
        }

        return expenseRepository.findByTrip(trip);
    }

    public void deleteExpense(UUID id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada."));

        if (!expense.getTrip().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta despesa.");
        }

        expenseRepository.delete(expense);
    }
}
