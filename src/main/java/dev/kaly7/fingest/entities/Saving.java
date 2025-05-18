package dev.kaly7.fingest.entities;

import dev.kaly7.fingest.entities.money.Money;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "saving")
public class Saving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "icon")
    private byte[] icon;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total"))
    @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    private Money total;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "current"))
    @AttributeOverride(name = "currency", column = @Column(name = "current_currency"))
    private Money current;

    @Column(name = "start_date")
    private LocalDate start;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

}
