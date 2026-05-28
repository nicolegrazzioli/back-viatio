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
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return user != null && currency != null && user.getId().equals(wallet.user.getId()) && currency.equals(wallet.currency);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

