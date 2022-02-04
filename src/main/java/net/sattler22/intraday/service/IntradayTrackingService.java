package net.sattler22.intraday.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Intraday Tracking Service Interface
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
public sealed interface IntradayTrackingService permits IntradayTrackingServiceInMemoryImpl {

    /**
     * Get an intraday security
     *
     * @param symbol The security's symbol (case insensitive)
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
     * @param symbol The security's symbol (case insensitive)
     * @param price The current price
     * @throws NullPointerException When the trade date is <code>NULL</code>
     * @throws NullPointerException When the symbol is <code>NULL</code>
     * @throws NullPointerException When the price is <code>NULL</code>
     */
    void book(LocalDate tradeDate, String symbol, BigDecimal price);

    /**
     * Intraday Security Interface
     */
    interface Security {

        /**
         * Get trade date
         *
         * @return The date the security was traded on
         */
        LocalDate tradeDate();

        /**
         * Get symbol
         *
         * @return The security's symbol in upper case
         */
        String symbol();

        /**
         * Get low price
         *
         * @return The low price of the day
         */
        BigDecimal lowPrice();

        /**
         * Get high price
         *
         * @return The high price of the day
         */
        BigDecimal highPrice();

        /**
         * Calculate average price
         *
         * @param roundingMode Indicates how the least significant digit is to be calculated. If
         *                     <code>NULL</code>, then <code>RoundingMode.HALF_UP</code> will be used.
         */
        BigDecimal calcAveragePrice(RoundingMode roundingMode);

        /**
         * Update price
         *
         * @param tradeDate The date the security was traded
         * @param price The current price
         * @return True if the update was successful. Otherwise, returns false.
         * @throws NullPointerException When the trade date is <code>NULL</code>
         * @throws NullPointerException When the price is <code>NULL</code>
         */
        boolean update(LocalDate tradeDate, BigDecimal price);
    }
}
