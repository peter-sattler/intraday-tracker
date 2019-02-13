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
public interface IntradayTrackingService {

    /**
     * Get an intraday security
     *
     * @param symbol The security's symbol (case insensitive)
     * @throws NullPointerException When the symbol is NULL
     * @throws IllegalArgumentException When the symbol can not be found
     */
    Security getSecurity(String symbol);

    /**
     * Get all intraday securities
     *
     * @return A collection of all intraday securities
     */
    Collection<Security> getSecurities();

    /**
     * Record an intraday security price
     *
     * @param tradeDate The date the security was traded
     * @param symbol The security's symbol (case insensitive)
     * @param price The current price
     * @throws NullPointerException When the trade date is NULL
     * @throws NullPointerException When the symbol is NULL
     * @throws NullPointerException When the price is NULL
     */
    void record(LocalDate tradeDate, String symbol, BigDecimal price);

    /**
     * Intraday Security Interface
     */
    interface Security {

        /**
         * Get the date the security was traded on
         */
        LocalDate getTradeDate();

        /**
         * Get the security's (upper cased) symbol
         */
        String getSymbol();

        /**
         * Get the low price of the day
         */
        BigDecimal getLowPrice();

        /**
         * Get the high price of the day
         */
        BigDecimal getHighPrice();

        /**
         * Calculates the average price of the day
         *
         * @param roundingMode Indicates how the least significant digit is to be calculated. If
         *        NULL, then <code>RoundingMode.HALF_UP</code> will be used.
         */
        BigDecimal calcAveragePrice(RoundingMode roundingMode);

        /**
         * Updates the security with a new price
         *
         * @param tradeDate The date the security was traded
         * @param price The current price
         * @return True if the update was successful. Otherwise, returns false.
         * @throws NullPointerException When the trade date is NULL
         * @throws NullPointerException When the price is NULL
         */
        boolean update(LocalDate tradeDate, BigDecimal price);
    }
}
