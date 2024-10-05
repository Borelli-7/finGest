package dev.kaly7.finGest.entities;

import java.math.BigDecimal;
import java.time.Instant;

public class ResultJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long resultID;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal amount;
    private Instant createdDate;
    private Instant updatedDate;
    @Column(nullable = false)
    private boolean inFlow;
    private int weekNumber;
    @Column(nullable = false)
    private boolean fixedOutflow;
}
