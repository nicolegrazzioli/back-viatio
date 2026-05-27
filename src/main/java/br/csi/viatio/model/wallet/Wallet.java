package br.csi.viatio.model.wallet;

import br.csi.viatio.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

