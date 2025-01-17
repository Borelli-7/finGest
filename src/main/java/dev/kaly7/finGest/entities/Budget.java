package dev.kaly7.finGest.entities;

import dev.kaly7.finGest.entities.money.Money;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BUDGET")
public class Budget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumns({
            @JoinColumn(name = "category_name", referencedColumnName = "name", nullable = false),
            @JoinColumn(name = "category_profit", referencedColumnName = "profit", nullable = false)
    })
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @Embedded
    @NonNull
    private Money total;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "start_date", nullable = false)),
            @AttributeOverride(name = "end", column = @Column(name = "end_date", nullable = false))
    })
    @NonNull
    private DateRange dateRange;
}
