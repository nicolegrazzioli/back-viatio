package br.csi.viatio.model.expense;

import br.csi.viatio.model.trip.Trip;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private String description;

    @Column(name = "photo_path")
    private String photoPath;
}

