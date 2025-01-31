package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.money.Money;

public record SummaryDto(Money inflow, Money outflow) {

}
