package br.csi.pilago.model.wallet;

import lombok.*;
import java.io.Serializable;

// chave primária composta (ID do usuário + código da moeda)

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WalletId implements Serializable {
    private Long user;
    private String currency;
}
