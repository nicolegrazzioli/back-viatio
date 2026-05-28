package br.csi.viatio.service;

import java.util.UUID;
import java.util.List;

import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.Expense;
import br.csi.viatio.repository.ExpenseRepository;
import br.csi.viatio.dto.expense.ExpenseRequest;
import br.csi.viatio.model.Trip;
import br.csi.viatio.repository.TripRepository;
import br.csi.viatio.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Classe de serviço responsável por conter as regras de negócio relativas a gastos de viagens
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final CurrencyTransactionService currencyTransactionService;

    // Registra uma nova despesa
    @Transactional
    public Expense createExpense(ExpenseRequest dados, User user) {
        // Busca a viagem à qual a despesa pertence. Joga erro 404 se a viagem não existir
        Trip trip = tripRepository.findById(dados.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));

        // Garante que o usuário logado é de fato o proprietário desta viagem
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para adicionar despesas nesta viagem.");
        }

        Expense expense = new Expense();
        expense.setId(dados.id());
        expense.setTrip(trip);
        expense.setTitle(dados.title());
        expense.setAmount(dados.amount());
        expense.setCurrency(dados.currency());
        expense.setCategory(dados.category());
        expense.setDate(dados.date());
        expense.setIsAverageCost(dados.isAverageCost());
        expense.setExchangeRate(dados.exchangeRate());
        expense.setAmountBrl(dados.amountBrl());
        expense.setPhotoPath(dados.photoPath());

        Expense saved = expenseRepository.save(expense);
        
        // Recalcula o saldo da carteira para a moeda do gasto atual
        currencyTransactionService.recalculateWallet(user, saved.getCurrency());
        
        return saved;
    }

    // Edita uma despesa existente
    @Transactional
    public Expense updateExpense(UUID id, ExpenseRequest dados, User user) {
        // Busca a despesa existente. Se não achar, retorna 404
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada."));

        // Garante que o usuário autenticado é o dono da despesa
        if (!expense.getTrip().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para editar esta despesa.");
        }

        // Guarda a moeda que estava cadastrada antes (caso o usuário troque a moeda do gasto)
        String oldCurrency = expense.getCurrency();

        // Se a viagem da despesa for alterada (transferir gasto), valida a nova viagem
        Trip trip = tripRepository.findById(dados.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Nova viagem não encontrada."));
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para associar a despesa a esta viagem.");
        }

        // Atualiza os dados
        expense.setTrip(trip);
        expense.setTitle(dados.title());
        expense.setAmount(dados.amount());
        expense.setCurrency(dados.currency());
        expense.setCategory(dados.category());
        expense.setDate(dados.date());
        expense.setIsAverageCost(dados.isAverageCost());
        expense.setExchangeRate(dados.exchangeRate());
        expense.setAmountBrl(dados.amountBrl());
        expense.setPhotoPath(dados.photoPath());

        Expense saved = expenseRepository.save(expense);
        
        // Recalcula o saldo da carteira para a moeda atual do gasto
        currencyTransactionService.recalculateWallet(user, saved.getCurrency());

        // Se o usuário mudou a moeda do gasto, recalcula também a carteira da moeda antiga
        if (!oldCurrency.equals(saved.getCurrency())) {
            currencyTransactionService.recalculateWallet(user, oldCurrency);
        }
        
        return saved;
    }

    // Recupera a lista de despesas de uma viagem, validando a posse
    @Transactional(readOnly = true)
    public List<Expense> listByTrip(UUID tripId, User user) {
        // Busca a viagem informada
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem não encontrada."));

        // Valida se a viagem pertence ao usuário ativo
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para ver as despesas desta viagem.");
        }

        // Retorna a lista de gastos da viagem
        return expenseRepository.findByTrip(trip);
    }

    // Remove uma despesa pelo ID
    @Transactional
    public void deleteExpense(UUID id, User user) {
        // Busca a despesa a ser removida. Se não achar, retorna 404
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada."));

        // Valida se o usuário autenticado é o dono da viagem associada a esta despesa
        if (!expense.getTrip().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta despesa.");
        }

        String currency = expense.getCurrency();

        // Remove a despesa do banco de dados
        expenseRepository.delete(expense);
        
        // Recalcula a carteira correspondente à moeda do gasto excluído para atualizar o saldo
        currencyTransactionService.recalculateWallet(user, currency);
    }
}
