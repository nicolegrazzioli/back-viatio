package br.csi.viatio.service;

import java.util.UUID;
import java.util.List;

import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.trip.Trip;
import br.csi.viatio.model.trip.TripRepository;
import br.csi.viatio.model.trip.TripRequest;
import br.csi.viatio.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    private final TripRepository repository;
    private final br.csi.viatio.model.expense.ExpenseRepository expenseRepository;
    private final CurrencyTransactionService currencyTransactionService;

    public TripService(TripRepository repository, br.csi.viatio.model.expense.ExpenseRepository expenseRepository, CurrencyTransactionService currencyTransactionService) {
        this.repository = repository;
        this.expenseRepository = expenseRepository;
        this.currencyTransactionService = currencyTransactionService;
    }

    public Trip createTrip(TripRequest dados, User user) {
        Trip trip;
        if (dados.id() != null) {
            trip = repository.findById(dados.id()).orElse(new Trip());
            trip.setId(dados.id());
        } else {
            trip = new Trip();
        }

        trip.setUser(user);
        trip.setTitle(dados.title());
        trip.setStartDate(dados.startDate());
        trip.setEndDate(dados.endDate());
        trip.setCoverType(dados.coverType());

        Trip savedTrip = repository.save(trip);
        
        if (dados.id() != null) {
            // Se for edição, pode ter mudado a data. Recalcular VET para as moedas da viagem.
            List<br.csi.viatio.model.expense.Expense> expenses = expenseRepository.findByTrip(savedTrip);
            expenses.stream().map(e -> e.getCurrency()).distinct().forEach(currency -> {
                currencyTransactionService.recalculateWallet(user, currency);
            });
        }
        
        return savedTrip;
    }

    public List<Trip> listByUser(User user) {
        return repository.findByUser(user);
    }

    public void deleteTrip(UUID id, User user) {
        Trip trip = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));
        
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta viagem.");
        }
        
        List<br.csi.viatio.model.expense.Expense> expenses = expenseRepository.findByTrip(trip);
        List<String> currencies = expenses.stream().map(e -> e.getCurrency()).distinct().toList();
        
        repository.delete(trip);
        
        currencies.forEach(currency -> {
            currencyTransactionService.recalculateWallet(user, currency);
        });
    }
}
