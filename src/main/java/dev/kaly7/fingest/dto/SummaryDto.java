package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.money.Money;

public record SummaryDto(Money inflow, Money outflow) {

//    public SummaryDto {
//        // Validate fields to ensure non-null values
//        if (inflow == null) {
//            throw new IllegalArgumentException("Inflow cannot be null");
//        }
//        if (outflow == null) {
//            throw new IllegalArgumentException("Outflow cannot be null");
//        }
//    }
}
