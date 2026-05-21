package net.sattler22.intraday.service;

import net.jcip.annotations.ThreadSafe;

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
     * @param symbol The security's symbol (case-insensitive)
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
     * @param tradeDate The date the security was traded
     * @param symbol The security's symbol (case-insensitive)
     * @param price The current price
     */
    void book(LocalDate tradeDate, String symbol, BigDecimal price);

    /**
     * Intraday Security
     */
    @ThreadSafe
    final class Security {

        private final LocalDate tradeDate;
        private final String symbol;
        private BigDecimal lowPrice;
        private BigDecimal highPrice;
        private int priceCount;
        private BigDecimal priceSum;
        private final Object lockObject = new Object();

        public Security(LocalDate tradeDate, String symbol, BigDecimal price) {
            this.tradeDate = Objects.requireNonNull(tradeDate, "Trade date is required");
            this.symbol = Objects.requireNonNull(symbol, "Symbol is required");
            this.lowPrice = Objects.requireNonNull(price, "Price is required");
            if (price.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("Price must be greater than zero");
            this.highPrice = price;
            this.priceCount = 1;
            this.priceSum = price;
        }

        public Security(Security source) {
            Objects.requireNonNull(source, "Source is required");
            synchronized (source.lockObject) {
                this.tradeDate = source.tradeDate;
                this.symbol = source.symbol;
                this.lowPrice = source.lowPrice;
                this.highPrice = source.highPrice;
                this.priceCount = source.priceCount;
                this.priceSum = source.priceSum;
            }
        }

        /**
         * Get trade date
         *
         * @return The date the security was traded on
         */
        public LocalDate tradeDate() {
            synchronized (lockObject) {
                return tradeDate;
            }
        }

        /**
         * Get symbol
         *
         * @return The security's symbol in upper case
         */
        public String symbol() {
            synchronized (lockObject) {
                return symbol;
            }
        }

        /**
         * Get low price
         *
         * @return The low price of the day
         */
        public BigDecimal lowPrice() {
            synchronized (lockObject) {
                return lowPrice;
            }
        }

        /**
         * Get high price
         *
         * @return The high price of the day
         */
        public BigDecimal highPrice() {
            synchronized (lockObject) {
                return highPrice;
            }
        }

        /**
         * Calculate average price
         *
         * @param roundingMode Indicates how the least significant digit is to be calculated. If {@code NULL}, then
         *                     {@code RoundingMode.HALF_UP} will be used.
         */
        public BigDecimal calcAveragePrice(RoundingMode roundingMode) {
            if (roundingMode == null)
                roundingMode = RoundingMode.HALF_UP;
            synchronized (lockObject) {
                return priceSum.divide(new BigDecimal(priceCount), 2, roundingMode);
            }
        }

        /**
         * Update price
         *
         * @param price The current price
         */
        public void update(BigDecimal price) {
            Objects.requireNonNull(price, "Price is required");
            synchronized (lockObject) {
                if (price.compareTo(lowPrice) < 0)
                    this.lowPrice = price;
                if (price.compareTo(highPrice) > 0)
                    this.highPrice = price;
                this.priceCount++;
                this.priceSum = priceSum.add(price);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(tradeDate, symbol);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (!(other instanceof Security that))
                return false;
            return Objects.equals(this.tradeDate, that.tradeDate()) && Objects.equals(this.symbol, that.symbol());
        }

        @Override
        public String toString() {
            return String.format("%s [tradeDate=%s, symbol=%s, lowPrice=%s, highPrice=%s, priceCount=%d, priceSum=%s]",
                    getClass().getSimpleName(), tradeDate, symbol, lowPrice, highPrice, priceCount, priceSum);
        }
    }
}
