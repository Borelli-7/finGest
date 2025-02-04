package dev.kaly7.fingest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "account")
public class User {
    @Getter
    @Id
    @Column(name = "login")
    private String login;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private char[] password;

    @Column(name = "admin")
    private boolean admin;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Wallet> wallets;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Budget> budgets;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Saving> savings;

    @Builder
    public User(String login, String firstName, String lastName, char[] password, boolean admin,
                List<Wallet> wallets, List<Budget> budgets, List<Saving> savings) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password.clone();  // Defensive copy
        this.admin = admin;
        this.wallets = List.copyOf(wallets);
        this.budgets = List.copyOf(budgets);
        this.savings = List.copyOf(savings);
    }
}
