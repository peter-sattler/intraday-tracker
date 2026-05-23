package net.sattler22.intraday.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

/**
 * Intraday Tracking Service
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
public sealed interface IntradayTrackingService permits IntradayTrackingServiceInMemoryImpl {

    /**
     * Get an intraday security
     *
     * @param symbol The security's symbol (case-insensitive). Only one date per security is retained.
     */
    Security security(String symbol);

    /**
     * Get all intraday securities
     *
     * @return A collection of all intraday securities
     */
    Collection<Security> securities();

    /**
     * Book an intraday security
     *
     * @param tradeDate The date the security was traded on
     * @param symbol The security's symbol (case-insensitive)
     * @param price The current price
     */
    void book(LocalDate tradeDate, String symbol, BigDecimal price);

    /**
     * Intraday Security
     */
    record Security(LocalDate tradeDate, String symbol, BigDecimal lowPrice, BigDecimal highPrice, int priceCount, BigDecimal priceSum) {

        public Security {
            Objects.requireNonNull(tradeDate, "Trade date is required");
            Objects.requireNonNull(symbol, "Symbol is required");
            if (symbol.isBlank())
                throw new IllegalArgumentException("Symbol is required");
            Objects.requireNonNull(lowPrice, "Low price is required");
            if (lowPrice.signum() <= 0)
                throw new IllegalArgumentException("Low price must be greater than zero");
            Objects.requireNonNull(highPrice, "High price is required");
            if (highPrice.signum() <= 0)
                throw new IllegalArgumentException("High price must be greater than zero");
            if (priceCount <= 0)
                throw new IllegalArgumentException("Price count must be greater than zero");
            Objects.requireNonNull(priceSum, "Price sum is required");
        }

        /**
         * Calculate average price
         *
         * @param roundingMode Indicates how the least significant digit is to be calculated. If {@code NULL}, then
         *                     {@code RoundingMode.HALF_UP} will be used.
         */
        public BigDecimal calcAveragePrice(RoundingMode roundingMode) {
            return priceSum.divide(BigDecimal.valueOf(priceCount), roundingMode == null ? RoundingMode.HALF_UP : roundingMode);
        }
    }
}
