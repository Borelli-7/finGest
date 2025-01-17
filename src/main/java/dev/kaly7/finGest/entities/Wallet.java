package dev.kaly7.finGest.entities;

import dev.kaly7.finGest.entities.money.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Embedded
    private Money amount;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wallet")
    private List<Expense> expenses;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @Builder
    public Wallet(Integer id, Money amount, String name, List<Expense> expenses, User user) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        // Make sure the list is non-null and immutable
        this.expenses = expenses == null ? List.of() : List.copyOf(expenses);
        this.user = user;
    }
}
