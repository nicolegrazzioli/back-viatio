package br.csi.viatio.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "currency_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTransaction {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount;
    private String currency;

    @Column(name = "amount_brl")
    private BigDecimal amountBrl;

    private String source;
    
    private LocalDate date;

    @Column(name = "vet_rate")
    private BigDecimal vetRate;

    @Column(name = "photo_path")
    private String photoPath;

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
        CurrencyTransaction that = (CurrencyTransaction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
