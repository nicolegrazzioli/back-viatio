package br.csi.viatio.model.wallet;

import lombok.*;
import java.io.Serializable;

// chave primÃ¡ria composta (ID do usuÃ¡rio + cÃ³digo da moeda)

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WalletId implements Serializable {
    private Long user;
    private String currency;
}

