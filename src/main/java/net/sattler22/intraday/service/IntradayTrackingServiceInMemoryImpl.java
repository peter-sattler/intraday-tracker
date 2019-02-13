package net.sattler22.intraday.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.intraday.model.IntradaySecurityImpl;

/**
 * Intraday Tracking Service In Memory Implementation
 *
 * @author Pete Sattler
 * @version February 12, 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class IntradayTrackingServiceInMemoryImpl implements IntradayTrackingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntradayTrackingServiceInMemoryImpl.class);
    private final Map<String, Security> securityDataMap = new ConcurrentSkipListMap<>();

    @Override
    public Security getSecurity(String symbol) {
        Objects.requireNonNull(symbol, "Symbol is required");
        final Security security = securityDataMap.get(symbol.toUpperCase());
        if (security == null)
            throw new IllegalArgumentException(String.format("Symbol [%s] not found", symbol));
        return security;
    }

    @Override
    public Collection<Security> getSecurities() {
        return Collections.unmodifiableMap(securityDataMap).values();
    }

    @Override
    public void record(LocalDate tradeDate, String symbol, BigDecimal price) {
        Objects.requireNonNull(symbol, "Symbol is required");
        final Security existingSecurity = securityDataMap.get(symbol.toUpperCase());
        if (existingSecurity == null) {
            final Security newSecurity = new IntradaySecurityImpl(tradeDate, symbol.toUpperCase(), price);
            securityDataMap.put(symbol, newSecurity);
            LOGGER.debug("Added {}", newSecurity);
        }
        else if (existingSecurity.getTradeDate().equals(tradeDate)) {
            existingSecurity.update(tradeDate, price);
            LOGGER.debug("Updated existing {}", existingSecurity);
        }
        else {
            final IntradaySecurityImpl securityReplacement = new IntradaySecurityImpl(tradeDate, symbol.toUpperCase(), price);
            securityDataMap.put(symbol, securityReplacement);
            LOGGER.debug("Replaced existing {}", securityReplacement);
        }
    }
}
