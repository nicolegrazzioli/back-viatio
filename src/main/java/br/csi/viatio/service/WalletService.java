package br.csi.viatio.service;

import br.csi.viatio.model.User;
import br.csi.viatio.model.wallet.Wallet;
import br.csi.viatio.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// Classe de serviço responsável pelas regras de negócio da carteira consolidada
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;

    // Busca no banco e retorna todas as carteiras e saldos pertencentes ao usuário informado
    @Transactional(readOnly = true)
    public List<Wallet> listByUser(User user) {
        return repository.findByUser(user);
    }
}

