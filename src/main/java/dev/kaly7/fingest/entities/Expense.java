package dev.kaly7.fingest.entities;

import dev.kaly7.fingest.entities.money.Money;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Embedded
    private Money amount;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumns({
            @JoinColumn(name = "category_name", referencedColumnName = "name", nullable = false),
            @JoinColumn(name = "category_profit", referencedColumnName = "profit", nullable = false)
    })
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    /**
     * Overrides toString for better logging and debugging.
     */
    @Override
    public String toString() {
        return String.format("Expense{id=%d, message='%s', date=%s, amount=%s, category=%s}",
                id, message, date, amount, category);
    }
}
