package br.csi.viatio.service;

import java.util.UUID;
import java.util.List;

import br.csi.viatio.model.Expense;
import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.Trip;
import br.csi.viatio.repository.TripRepository;
import br.csi.viatio.dto.trip.TripRequest;
import br.csi.viatio.model.User;
import br.csi.viatio.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Classe de serviço responsável por conter a lógica de negócios referente às viagens
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository repository;
    private final ExpenseRepository expenseRepository;
    private final CurrencyTransactionService currencyTransactionService;

    // Cria uma nova viagem
    @Transactional
    public Trip createTrip(TripRequest dados, User user) {
        Trip trip = new Trip();
        trip.setId(dados.id());
        trip.setUser(user);
        trip.setTitle(dados.title());
        trip.setStartDate(dados.startDate());
        trip.setEndDate(dados.endDate());
        trip.setCoverType(dados.coverType());

        return repository.save(trip);
    }

    // Edita uma viagem existente
    @Transactional
    public Trip updateTrip(UUID id, TripRequest dados, User user) {
        // Busca a viagem pelo ID recebido na URL
        Trip trip = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));
        
        // Garante que o usuário logado é o dono da viagem
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para editar esta viagem.");
        }

        // Atualiza os dados da entidade
        trip.setTitle(dados.title());
        trip.setStartDate(dados.startDate());
        trip.setEndDate(dados.endDate());
        trip.setCoverType(dados.coverType());

        // Salva as alterações
        Trip savedTrip = repository.save(trip);
        
        // Recalcula o VET das moedas dessa viagem, pois a alteração das datas pode afetar a cotação
        List<Expense> expenses = expenseRepository.findByTrip(savedTrip);
        expenses.stream().map(e -> e.getCurrency()).distinct().forEach(currency -> {
            currencyTransactionService.recalculateWallet(user, currency);
        });
        
        return savedTrip;
    }

    // Lista todas as viagens salvas que pertencem a um determinado usuário
    @Transactional(readOnly = true)
    public List<Trip> listByUser(User user) {
        return repository.findByUser(user);
    }

    // Remove uma viagem do banco de dados e limpa seus vínculos
    @Transactional
    public void deleteTrip(UUID id, User user) {
        // Busca a viagem pelo ID. Se não encontrar, joga exceção de Recurso Não Encontrado (404)
        Trip trip = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));
        
        // Garante que o usuário logado só pode deletar as suas próprias viagens
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta viagem.");
        }
        
        // Busca todas as despesas vinculadas a essa viagem antes de deletá-la
        List<Expense> expenses = expenseRepository.findByTrip(trip);
        List<String> currencies = expenses.stream().map(e -> e.getCurrency()).distinct().toList();
        
        // Exclui todas as despesas da viagem e, em seguida, a própria viagem do banco
        expenseRepository.deleteAll(expenses);
        repository.delete(trip);
        
        // Como apagamos os gastos, o saldo e VET das moedas dessa viagem precisam ser recalculados
        currencies.forEach(currency -> {
            currencyTransactionService.recalculateWallet(user, currency);
        });
    }
}
