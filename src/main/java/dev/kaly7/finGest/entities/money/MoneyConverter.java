package dev.kaly7.finGest.entities.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MoneyConverter {

    private static final String BASE_CURRENCY = "USD";
    private static final Map<String, BigDecimal> CONVERSION_RATES = new ConcurrentHashMap<>();

    static {
        CONVERSION_RATES.put(BASE_CURRENCY, BigDecimal.ONE);
    }

    private MoneyConverter() {
        // Private constructor to prevent instantiation
    }

    static BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (amount == null || from == null || to == null) {
            throw new IllegalArgumentException("Amount and currencies must not be null");
        }

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }

        return amount.multiply(getRate(from, to));
    }

    private static BigDecimal getRate(Currency from, Currency to) {
        BigDecimal fromRate = CONVERSION_RATES.get(from.getCurrencyCode());
        BigDecimal toRate = CONVERSION_RATES.get(to.getCurrencyCode());

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Conversion rate not available for one or both currencies");
        }

        return fromRate.divide(toRate, RoundingMode.HALF_UP);
    }

    static void addConversionRate(Currency currency, BigDecimal rate) {
        if (currency == null || rate == null || rate.signum() <= 0) {
            throw new IllegalArgumentException("Currency and rate must not be null, and rate must be positive");
        }
        CONVERSION_RATES.put(currency.getCurrencyCode(), rate);
    }

    static void removeConversionRate(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        CONVERSION_RATES.remove(currency.getCurrencyCode());
    }
}