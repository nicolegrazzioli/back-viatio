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
import org.springframework.stereotype.Service;

// Classe de serviço responsável por conter a lógica de negócios referente às viagens
@Service
public class TripService {

    private final TripRepository repository;
    private final ExpenseRepository expenseRepository;
    private final CurrencyTransactionService currencyTransactionService;

    // Construtor padrão utilizado pelo Spring para injetar os repositórios e serviços necessários
    public TripService(TripRepository repository, ExpenseRepository expenseRepository, CurrencyTransactionService currencyTransactionService) {
        this.repository = repository;
        this.expenseRepository = expenseRepository;
        this.currencyTransactionService = currencyTransactionService;
    }

    // Cria ou edita uma viagem
    public Trip createTrip(TripRequest dados, User user) {
        Trip trip;
        // Se a requisição contiver um ID, significa que é uma atualização de viagem existente
        if (dados.id() != null) {
            trip = repository.findById(dados.id()).orElse(new Trip());
            trip.setId(dados.id());
        } else {
            // Caso contrário, é um novo cadastro
            trip = new Trip();
        }

        // Preenche/atualiza os dados da entidade
        trip.setUser(user);
        trip.setTitle(dados.title());
        trip.setStartDate(dados.startDate());
        trip.setEndDate(dados.endDate());
        trip.setCoverType(dados.coverType());

        // Salva as alterações no banco de dados (tabela trips)
        Trip savedTrip = repository.save(trip);
        
        // Se foi uma edição, recalcula o VET das moedas dessa viagem, pois a alteração das datas da viagem pode afetar o cálculo
        if (dados.id() != null) {
            List<Expense> expenses = expenseRepository.findByTrip(savedTrip);
            expenses.stream().map(e -> e.getCurrency()).distinct().forEach(currency -> {
                currencyTransactionService.recalculateWallet(user, currency);
            });
        }
        
        return savedTrip;
    }

    // Lista todas as viagens salvas que pertencem a um determinado usuário
    public List<Trip> listByUser(User user) {
        return repository.findByUser(user);
    }

    // Remove uma viagem do banco de dados e limpa seus vínculos
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
