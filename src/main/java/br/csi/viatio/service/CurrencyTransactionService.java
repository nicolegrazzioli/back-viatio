package br.csi.viatio.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.List;

import br.csi.viatio.model.Trip;
import br.csi.viatio.infra.exception.ForbiddenException;
import br.csi.viatio.infra.exception.ResourceNotFoundException;
import br.csi.viatio.model.CurrencyTransaction;
import br.csi.viatio.repository.CurrencyTransactionRepository;
import br.csi.viatio.dto.currencytransaction.CurrencyTransactionRequest;
import br.csi.viatio.model.User;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.model.wallet.WalletId;
import br.csi.viatio.repository.TripRepository;
import br.csi.viatio.repository.WalletRepository;
import br.csi.viatio.model.Expense;
import br.csi.viatio.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

// regras de negócio das transações de moedas e cálculo do VET
@Service
public class CurrencyTransactionService {

    private final CurrencyTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;

    // Construtor com as dependências do banco injetadas pelo Spring
    public CurrencyTransactionService(CurrencyTransactionRepository transactionRepository, WalletRepository walletRepository, ExpenseRepository expenseRepository, TripRepository tripRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
    }

    // Recalcula o saldo e a taxa média (VET) da carteira do usuário para uma moeda estrangeira
    public void recalculateWallet(User user, String currency) {
        // Busca todas as compras de moedas do usuário na moeda
        List<CurrencyTransaction> txs = transactionRepository.findByUserAndCurrency(user, currency);
        WalletId walletId = new WalletId(user.getId(), currency);
        
        // Se não houver compras registradas para essa moeda, remove a carteira correspondente
        if (txs.isEmpty()) {
            walletRepository.findById(walletId).ifPresent(walletRepository::delete);
            return;
        }

        BigDecimal totalBought = BigDecimal.ZERO;
        BigDecimal totalBrl = BigDecimal.ZERO;

        // Soma o total da moeda comprada e o total em reais gasto nessas compras
        for (CurrencyTransaction tx : txs) {
            totalBought = totalBought.add(tx.getAmount());
            totalBrl = totalBrl.add(tx.getAmountBrl());
        }

        // Busca todas as despesas que o usuário cadastrou nessa moeda em todas as suas viagens
        List<Expense> expenses = expenseRepository.findByTrip_UserAndCurrency(user, currency);
        BigDecimal totalSpent = BigDecimal.ZERO;
        for (Expense exp : expenses) {
            totalSpent = totalSpent.add(exp.getAmount());
        }

        // Calcula o saldo restante (Moedas Compradas - Moedas Gastas)
        BigDecimal remainingBalance = totalBought.subtract(totalSpent);

        // Busca a carteira existente ou cria uma nova com saldo zerado
        Wallet wallet = walletRepository.findById(walletId).orElseGet(() -> {
            Wallet w = new Wallet();
            w.setUser(user);
            w.setCurrency(currency);
            w.setBalance(BigDecimal.ZERO);
            w.setAverageVet(BigDecimal.ZERO);
            return w;
        });
        
        BigDecimal newVet = BigDecimal.ZERO;
        wallet.setBalance(remainingBalance);
        
        // Calcula a taxa de câmbio média (Valor total em reais / Total comprado na moeda estrangeira)
        if (totalBought.compareTo(BigDecimal.ZERO) > 0) {
            newVet = totalBrl.divide(totalBought, 4, RoundingMode.HALF_UP);
        }
        
        wallet.setAverageVet(newVet);
        walletRepository.save(wallet);

        // Atualização em cascata do VET de cada viagem para os gastos marcados como "Custo Médio"
        List<Trip> trips = tripRepository.findByUser(user);
        for (Trip trip : trips) {
            java.time.LocalDate cutoffDate = trip.getEndDate();
            
            BigDecimal tripBought = BigDecimal.ZERO;
            BigDecimal tripBrl = BigDecimal.ZERO;
            
            // Filtra as compras de moeda que aconteceram até a data final daquela viagem específica
            for (CurrencyTransaction tx : txs) {
                if (cutoffDate == null || !tx.getDate().isAfter(cutoffDate)) {
                    tripBought = tripBought.add(tx.getAmount());
                    tripBrl = tripBrl.add(tx.getAmountBrl());
                }
            }
            
            BigDecimal tripVet = BigDecimal.ZERO;
            if (tripBought.compareTo(BigDecimal.ZERO) > 0) {
                tripVet = tripBrl.divide(tripBought, 4, RoundingMode.HALF_UP);
            } else {
                // Se não houver compras para a viagem antes de ela terminar, o VET padrão é 0.0 
                // Isso evita reescrever cotações manuais dos gastos para 1.0 nas atualizações
                tripVet = BigDecimal.ZERO;
            }
            
            // Só executa o update no banco se tivermos um VET médio válido calculado para a viagem
            if (tripVet.compareTo(BigDecimal.ZERO) > 0) {
                expenseRepository.updateTripExpensesVet(trip.getId(), currency, tripVet);
            }
        }
    }

    // Registra uma nova compra de moeda estrangeira ou atualiza uma existente
    public CurrencyTransaction createTransaction(CurrencyTransactionRequest dados, User user) {
        CurrencyTransaction transaction;
        // Se a requisição contiver ID, busca a transação no banco (modo edição)
        if (dados.id() != null) {
            transaction = transactionRepository.findById(dados.id()).orElse(new CurrencyTransaction());
            transaction.setId(dados.id());
        } else {
            // Caso contrário, instancia um novo objeto de compra de moeda
            transaction = new CurrencyTransaction();
        }
        
        // Popula as informações da transação
        transaction.setUser(user);
        transaction.setAmount(dados.amount());
        transaction.setCurrency(dados.currency());
        transaction.setAmountBrl(dados.amountBrl());
        transaction.setSource(dados.source());
        transaction.setDate(dados.date());
        transaction.setVetRate(dados.vetRate());
        transaction.setPhotoPath(dados.photoPath());

        // Salva a transação no banco
        transaction = transactionRepository.save(transaction);
        
        // Executa o recálculo dos saldos e cotações médias
        recalculateWallet(user, dados.currency());
        return transaction;
    }

    // Retorna a lista de todas as compras de moeda cadastradas para o usuário fornecido
    public List<CurrencyTransaction> listByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    // Remove um lançamento de compra de moeda do banco
    public void deleteTransaction(UUID id, User user) {
        // Busca a transação ou joga erro 404
        CurrencyTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        // Valida se o usuário que fez a requisição é o dono do registro
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar esta transação.");
        }

        String currency = transaction.getCurrency();
        
        // Exclui a transação do banco de dados
        transactionRepository.delete(transaction);
        
        // Recalcula o saldo da carteira da moeda afetada
        recalculateWallet(user, currency);
    }
}
