package dev.kaly7.fingest.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "category")
@EqualsAndHashCode
@IdClass(Category.CategoryPK.class)
public class Category {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class CategoryPK implements Serializable {
        @Column(name = "name", nullable = false)
        @NotNull
        private String name;

        @Column(name = "profit", nullable = false)
        @NotNull
        private Boolean profit;

        @Override
        public String toString() {
            return "CategoryPK{name='" + name + "', profit=" + profit + "}";
        }
    }

    @Id
    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Id
    @Column(name = "profit", nullable = false)
    @NotNull
    private Boolean profit;

    @Override
    public String toString() {
        return "Category{name='" + name + "', profit=" + profit + "}";
    }

    public Category(@NotNull String name, @NotNull Boolean profit) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.profit = Objects.requireNonNull(profit, "Profit must not be null");
    }

    @OneToMany(mappedBy = "category")
    private List<Expense> expenses;

    @OneToMany(mappedBy = "category")
    private List<Budget> budgets;
}