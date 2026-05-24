package net.sattler22.intraday.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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
     * @param symbol The security's symbol (case-insensitive).
     */
    Security security(String symbol);

    /**
     * Get all intraday securities
     *
     * @return An immutable list of all intraday securities sorted by symbol
     */
    List<Security> securities();

    /**
     * Book an intraday security
     *
     * <p>NOTE: Booking a symbol for a different trade date replaces the previously retained data for that symbol.</p>
     *
     * @param tradeDate The date the security was traded on
     * @param symbol The security's symbol (case-insensitive)
     * @param price The current price
     */
    void book(LocalDate tradeDate, String symbol, BigDecimal price);

    /**
     * Intraday Security
     */
    record Security(LocalDate tradeDate, String symbol, BigDecimal lowPrice, BigDecimal highPrice, long priceCount, BigDecimal priceSum) {

        public Security {
            Objects.requireNonNull(tradeDate, "Trade date is required");
            Objects.requireNonNull(symbol, "Symbol is required");
            symbol = symbol.strip().toUpperCase(Locale.ROOT);
            if (symbol.isEmpty())
                throw new IllegalArgumentException("Symbol is required");
            Objects.requireNonNull(lowPrice, "Low price is required");
            if (lowPrice.signum() <= 0)
                throw new IllegalArgumentException("Low price must be greater than zero");
            Objects.requireNonNull(highPrice, "High price is required");
            if (highPrice.signum() <= 0)
                throw new IllegalArgumentException("High price must be greater than zero");
            if (lowPrice.compareTo(highPrice) > 0)
                throw new IllegalArgumentException("Low price cannot exceed high price");
            if (priceCount <= 0)
                throw new IllegalArgumentException("Price count must be greater than zero");
            Objects.requireNonNull(priceSum, "Price sum is required");
            if (priceSum.signum() == 0)
                throw new IllegalArgumentException("Price sum must be greater than zero");
        }

        /**
         * Calculate average price
         *
         * @param scale The scale of the average price to be returned.
         * @param roundingMode Indicates how the least significant digit is to be calculated. If {@code NULL}, then
         *                     {@code RoundingMode.HALF_UP} will be used.
         */
        public BigDecimal calcAveragePrice(int scale, RoundingMode roundingMode) {
            if (scale < 0)
                throw new IllegalArgumentException("Scale cannot be negative");
            return priceSum.divide(BigDecimal.valueOf(priceCount), scale, roundingMode == null ? RoundingMode.HALF_UP : roundingMode);
        }
    }
}
