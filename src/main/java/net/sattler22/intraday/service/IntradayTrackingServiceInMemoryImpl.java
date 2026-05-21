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
    private final Map<String, Security> securityDataMap = new ConcurrentHashMap<>();

    @Override
    public Security security(String symbol) {
        final Security security = securityDataMap.get(normalizeSymbol(symbol));
        if (security == null)
            throw new IllegalArgumentException(String.format("Symbol [%s] not found", symbol));
        return new Security(security);  //Provide stable snapshot
    }

    @Override
    public Collection<Security> securities() {
        return securityDataMap.values().stream()
                .sorted(Comparator.comparing(Security::symbol))
                .toList();
    }

    @Override
    public void book(LocalDate tradeDate, String symbol, BigDecimal price) {
        Objects.requireNonNull(tradeDate, "Trade date is required");
        Objects.requireNonNull(price, "Price is required");
        if (price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Price must be greater than zero");
        //For the given symbol, either create, replace or update the existing price:
        //NOTE: Remapping function is guaranteed to execute atomically for each key!!!
        final String normalizedSymbol = normalizeSymbol(symbol);
        securityDataMap.compute(normalizedSymbol, (key, currentSecurity) -> {
            //New trade date or symbol:
            if (currentSecurity == null || !currentSecurity.tradeDate().equals(tradeDate)) {
                final Security newSecurity = new Security(tradeDate, symbol, price);
                logger.debug("{} {}", currentSecurity == null ? "Added" : "Replaced existing", newSecurity);
                return newSecurity;
            }
            //Update existing (same trade date) with new price:
            currentSecurity.update(price);
            logger.debug("Updated existing {}", currentSecurity);
            return currentSecurity;
        });
    }

    private static String normalizeSymbol(String symbol) {
        Objects.requireNonNull(symbol, "Symbol is required");
        return symbol.toUpperCase(Locale.ROOT);  //Locale neutral and deterministic
    }
}
