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
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private String title;
    
    private BigDecimal amount;
    private String currency;
    private String category;
    
    private LocalDate date;

    @Column(name = "is_average_cost")
    private Boolean isAverageCost;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "amount_brl")
    private BigDecimal amountBrl;

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
        Expense expense = (Expense) o;
        return id != null && id.equals(expense.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
