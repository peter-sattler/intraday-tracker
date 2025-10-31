package net.sattler22.intraday.service;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Intraday Tracking Service In Memory Implementation
 *
 * @author Pete Sattler
 * @version October 2025
 * @since February 12, 2019
 */
@ThreadSafe
public final class IntradayTrackingServiceInMemoryImpl implements IntradayTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(IntradayTrackingServiceInMemoryImpl.class);
    private final Map<String, Security> securityDataMap = new ConcurrentSkipListMap<>();

    @Override
    public Security security(String symbol) {
        Objects.requireNonNull(symbol, "Symbol is required");
        final Security security = securityDataMap.get(symbol.toUpperCase());
        if (security == null)
            throw new IllegalArgumentException(String.format("Symbol [%s] not found", symbol));
        return security;
    }

    @Override
    public Collection<Security> securities() {
        return Collections.unmodifiableMap(securityDataMap).values();
    }

    @Override
    public void book(LocalDate tradeDate, String symbol, BigDecimal price) {
        Objects.requireNonNull(symbol, "Symbol is required");
        final Security existingSecurity = securityDataMap.get(symbol.toUpperCase());
        if (existingSecurity == null) {
            final Security newSecurity = new Security(tradeDate, symbol.toUpperCase(), price);
            securityDataMap.put(symbol, newSecurity);
            logger.debug("Added {}", newSecurity);
        }
        else if (existingSecurity.tradeDate().equals(tradeDate)) {
            existingSecurity.update(price);
            logger.debug("Updated existing {}", existingSecurity);
        }
        else {
            final Security securityReplacement = new Security(tradeDate, symbol.toUpperCase(), price);
            securityDataMap.put(symbol, securityReplacement);
            logger.debug("Replaced existing {}", securityReplacement);
        }
    }
}
