package dev.kaly7.finGest.entities;

import java.math.BigDecimal;
import java.time.Instant;

public class BalanceJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long balanceID;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private double price;
    private Instant createdDate;
    @Column(nullable = false)
    private boolean assets;
    @Column(nullable = false)
    private BigDecimal amount;
}
