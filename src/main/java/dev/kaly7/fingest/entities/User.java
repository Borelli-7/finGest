package dev.kaly7.fingest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
//@Getter
//@Setter
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setPassword(char[] password) {
        this.password = password.clone();  // Defensive copy
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = List.copyOf(wallets);
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = List.copyOf(budgets);
    }

    public void setSavings(List<Saving> savings) {
        this.savings = List.copyOf(savings);
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public char[] getPassword() {
        return password.clone();  // Defensive copy
    }

    public List<Wallet> getWallets() {
        return List.copyOf(wallets);
    }

    public List<Budget> getBudgets() {
        return List.copyOf(budgets);
    }

    public List<Saving> getSavings() {
        return List.copyOf(savings);
    }



}
