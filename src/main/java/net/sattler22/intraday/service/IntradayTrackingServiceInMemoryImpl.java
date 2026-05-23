package net.sattler22.intraday.service;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
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
    public Collection<Security> securities() {
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
            //New trade date or symbol:
            if (currentAccumulator == null || !currentAccumulator.tradeDate().equals(tradeDate)) {
                final SecurityAccumulator newAccumulator = new SecurityAccumulator(tradeDate, normalizedSymbol, price);
                logger.debug("{} {}", currentAccumulator == null ? "Added" : "Replaced existing", newAccumulator.snapshot());
                return newAccumulator;
            }
            //Update existing (same trade date) with new price:
            currentAccumulator.update(price);
            logger.debug("Updated existing {}", currentAccumulator.snapshot());
            return currentAccumulator;
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

    private static final class SecurityAccumulator {

        private final LocalDate tradeDate;
        private final String symbol;
        private BigDecimal lowPrice;
        private BigDecimal highPrice;
        private int priceCount;
        private BigDecimal priceSum;

        private SecurityAccumulator(LocalDate tradeDate, String symbol, BigDecimal price) {
            this.tradeDate = Objects.requireNonNull(tradeDate, "Trade date is required");
            this.symbol = Objects.requireNonNull(symbol, "Symbol is required");
            validatePrice(price);
            this.lowPrice = price;
            this.highPrice = price;
            this.priceCount = 1;
            this.priceSum = price;
        }

        private LocalDate tradeDate() {
            return tradeDate;
        }

        private void update(BigDecimal price) {
            validatePrice(price);
            if (price.compareTo(lowPrice) < 0)
                this.lowPrice = price;
            if (price.compareTo(highPrice) > 0)
                this.highPrice = price;
            this.priceCount++;
            this.priceSum = priceSum.add(price);
        }

        private Security snapshot() {
            return new Security(tradeDate, symbol, lowPrice, highPrice, priceCount, priceSum);
        }
    }
}
