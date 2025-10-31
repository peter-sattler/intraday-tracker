package net.sattler22.intraday.service;

import net.jcip.annotations.ThreadSafe;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Intraday Tracking Service Interface
 *
 * @author Pete Sattler
 * @version October 2025
 * @since February 12, 2019
 */
public sealed interface IntradayTrackingService permits IntradayTrackingServiceInMemoryImpl {

    /**
     * Get an intraday security
     *
     * @param symbol The security's symbol (case-insensitive)
     * @throws NullPointerException When the symbol is <code>NULL</code>
     * @throws IllegalArgumentException When the symbol can not be found
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
     * @throws NullPointerException When the trade date is <code>NULL</code>
     * @throws NullPointerException When the symbol is <code>NULL</code>
     * @throws NullPointerException When the price is <code>NULL</code>
     */
    void book(LocalDate tradeDate, String symbol, BigDecimal price);

    /**
     * Intraday Security
     */
    @ThreadSafe
    final class Security {

        private final LocalDate tradeDate;
        private final String symbol;
        private volatile BigDecimal lowPrice;
        private volatile BigDecimal highPrice;
        private volatile BigDecimal priceSum;
        private final List<BigDecimal> prices = new LinkedList<>();
        private final Object lockObject = new Object();

        public Security(LocalDate tradeDate, String symbol, BigDecimal price) {
            this.tradeDate = Objects.requireNonNull(tradeDate, "Trade date is required");
            this.symbol = Objects.requireNonNull(symbol, "Symbol is required");
            this.lowPrice = Objects.requireNonNull(price, "Price is required");
            if (price.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("Price must be greater than zero");
            this.highPrice = price;
            this.priceSum = price;
            this.prices.add(price);
        }

        /**
         * Get trade date
         *
         * @return The date the security was traded on
         */
        public LocalDate tradeDate() {
            return tradeDate;
        }


        /**
         * Get symbol
         *
         * @return The security's symbol in upper case
         */
        public String symbol() {
            return symbol;
        }

        /**
         * Get low price
         *
         * @return The low price of the day
         */
        public BigDecimal lowPrice() {
            return lowPrice;
        }

        /**
         * Get high price
         *
         * @return The high price of the day
         */
        public BigDecimal highPrice() {
            return highPrice;
        }

        /**
         * Calculate average price
         *
         * @param roundingMode Indicates how the least significant digit is to be calculated. If <code>NULL</code>,
         *                     then <code>RoundingMode.HALF_UP</code> will be used.
         */
        public BigDecimal calcAveragePrice(RoundingMode roundingMode) {
            if (roundingMode == null)
                roundingMode = RoundingMode.HALF_UP;  //Per interface contract
            synchronized (lockObject) {
                return priceSum.divide(new BigDecimal(prices.size()), roundingMode);
            }
        }

        /**
         * Update price
         *
         * @param price The current price
         * @throws NullPointerException When the trade date is <code>NULL</code>
         * @throws NullPointerException When the price is <code>NULL</code>
         */
        public void update(BigDecimal price) {
            Objects.requireNonNull(price, "Price is required");
            synchronized (lockObject) {
                if (price.compareTo(lowPrice) < 0)
                    this.lowPrice = price;
                if (price.compareTo(highPrice) > 0)
                    this.highPrice = price;
                this.priceSum = priceSum.add(price);
                this.prices.add(price);
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
            if (other == null)
                return false;
            if (this.getClass() != other.getClass())
                return false;
            final Security that = (Security) other;
            return Objects.equals(this.tradeDate, that.tradeDate()) && Objects.equals(this.symbol, that.symbol());
        }

        @Override
        public String toString() {
            return String.format("%s [tradeDate=%s, symbol=%s, lowPrice=%s, highPrice=%s, priceSum=%s, prices=%s]",
                    getClass().getSimpleName(), tradeDate, symbol, lowPrice, highPrice, priceSum, prices);
        }
    }
}
