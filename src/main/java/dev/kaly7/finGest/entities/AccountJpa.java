package dev.kaly7.finGest.entities;

import java.math.BigDecimal;
import java.time.Instant;


public class AccountJpa {

    private String accountId;
    private String name;
    private Integer percentage;
    private Instant updatedDate;
    private BigDecimal debitedAmount;
    private BigDecimal creditedAmount;
    private Integer monthNumber;
    private User owner;
}
