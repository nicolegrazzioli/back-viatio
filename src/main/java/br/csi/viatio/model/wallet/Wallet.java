package br.csi.viatio.model.wallet;

import br.csi.viatio.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@IdClass(WalletId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    private String currency;

    private BigDecimal balance;

    @Column(name = "average_vet")
    private BigDecimal averageVet;
}

