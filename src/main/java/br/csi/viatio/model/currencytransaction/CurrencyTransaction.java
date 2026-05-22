package br.csi.viatio.model.currencytransaction;

import java.util.UUID;

import br.csi.viatio.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "currency_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    private String description;

    @Column(name = "photo_path")
    private String photoPath;
}

