package dev.kaly7.finGest.entities.money;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class Money implements Comparable<Money>, Serializable {

    private static final String DEFAULT_CURRENCY = "USD";
    public static final Money ZERO = new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currencyCode) {
        this.amount = validateAmount(amount);
        this.currency = Currency.getInstance(ofNullable(currencyCode).orElse(DEFAULT_CURRENCY));
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = validateAmount(amount);
        this.currency = Objects.requireNonNull(currency, "Currency must not be null");
    }

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Other Money object must not be null");
        Money converted = other.convertTo(currency);
        return amount.compareTo(converted.amount);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Other Money object must not be null");
        Money converted = other.convertTo(currency);
        BigDecimal newAmount = amount.add(converted.amount);
        return new Money(newAmount, currency);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Other Money object must not be null");
        Money converted = other.convertTo(currency);
        BigDecimal newAmount = amount.subtract(converted.amount);
        return new Money(newAmount, currency);
    }

    private Money convertTo(Currency newCurrency) {
        Objects.requireNonNull(newCurrency, "New currency must not be null");
        return currency.equals(newCurrency) ? this : new Money(MoneyConverter.convert(amount, currency, newCurrency), newCurrency);
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be null or negative");
        }
        return amount;
    }
}