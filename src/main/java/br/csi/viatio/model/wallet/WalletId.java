package br.csi.viatio.model.wallet;

import lombok.*;
import java.io.Serializable;

// chave primária composta (ID do usuário + código da moeda)

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WalletId implements Serializable {
    private UUID user;
    private String currency;
}

