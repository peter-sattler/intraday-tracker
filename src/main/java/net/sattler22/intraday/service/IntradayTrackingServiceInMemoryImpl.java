package net.sattler22.intraday.service;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intraday Tracking Service In Memory Implementation
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
@ThreadSafe
public final class IntradayTrackingServiceInMemoryImpl implements IntradayTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(IntradayTrackingServiceInMemoryImpl.class);
    private final Map<String, SecurityAccumulator> symbolAccumulatorMap = new ConcurrentHashMap<>();

    @Override
    public Security security(String symbol) {
        final SecurityAccumulator accumulator = symbolAccumulatorMap.get(normalizeSymbol(symbol));
        if (accumulator == null)
            throw new IllegalArgumentException(String.format("Symbol [%s] not found", symbol));
        return accumulator.snapshot();  //Provide stable snapshot
    }

    @Override
    public List<Security> securities() {
        return symbolAccumulatorMap.values().stream()
                .map(SecurityAccumulator::snapshot)
                .sorted(Comparator.comparing(Security::symbol))
                .toList();  //Provide stable, sorted snapshot
    }

    @Override
    public void book(LocalDate tradeDate, String symbol, BigDecimal price) {
        Objects.requireNonNull(tradeDate, "Trade date is required");
        validatePrice(price);
        //For the given symbol, either create, replace or update the existing price:
        //NOTE: Remapping function is guaranteed to execute atomically for each key!!!
        final String normalizedSymbol = normalizeSymbol(symbol);
        symbolAccumulatorMap.compute(normalizedSymbol, (key, currentAccumulator) -> {
            //First price for symbol or replace prior trade date:
            if (currentAccumulator == null || !currentAccumulator.tradeDate().equals(tradeDate)) {
                final SecurityAccumulator newAccumulator = new SecurityAccumulator(tradeDate, normalizedSymbol, price);
                if (logger.isDebugEnabled())
                    logger.debug("{} {}", currentAccumulator == null ? "Added" : "Replaced existing", newAccumulator.snapshot());
                return newAccumulator;
            }
            //Update existing (same trade date) with new price:
            final SecurityAccumulator updatedAccumulator = currentAccumulator.withPrice(price);
            if (logger.isDebugEnabled())
                logger.debug("Updated existing {}", updatedAccumulator.snapshot());
            return updatedAccumulator;
        });
    }

    private static String normalizeSymbol(String symbol) {
        Objects.requireNonNull(symbol, "Symbol is required");
        final String normalizedSymbol = symbol.strip().toUpperCase(Locale.ROOT);  //Locale neutral and deterministic
        if (normalizedSymbol.isEmpty())
            throw new IllegalArgumentException("Symbol is required");
        return normalizedSymbol;
    }

    private static void validatePrice(BigDecimal price) {
        Objects.requireNonNull(price, "Price is required");
        if (price.signum() <= 0)
            throw new IllegalArgumentException("Price must be greater than zero");
    }

    @Immutable
    private static final class SecurityAccumulator {

        private final LocalDate tradeDate;
        private final String symbol;
        private final BigDecimal lowPrice;
        private final BigDecimal highPrice;
        private final int priceCount;
        private final BigDecimal priceSum;

        private SecurityAccumulator(LocalDate tradeDate, String symbol, BigDecimal price) {
            this(Objects.requireNonNull(tradeDate, "Trade date is required"),
                    Objects.requireNonNull(symbol, "Symbol is required"), price, price, 1, price);
            validatePrice(price);
        }

        private SecurityAccumulator(LocalDate tradeDate, String symbol, BigDecimal lowPrice, BigDecimal highPrice,
                                    int priceCount, BigDecimal priceSum) {
            this.tradeDate = tradeDate;
            this.symbol = symbol;
            this.lowPrice = lowPrice;
            this.highPrice = highPrice;
            this.priceCount = priceCount;
            this.priceSum = priceSum;
        }

        private LocalDate tradeDate() {
            return tradeDate;
        }

        private SecurityAccumulator withPrice(BigDecimal price) {
            validatePrice(price);
            return new SecurityAccumulator(tradeDate, symbol, price.min(lowPrice), price.max(highPrice), priceCount + 1, priceSum.add(price));
        }

        private Security snapshot() {
            return new Security(tradeDate, symbol, lowPrice, highPrice, priceCount, priceSum);
        }
    }
}
