package net.sattler22.intraday.service;

import net.sattler22.intraday.TestStockTickers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static net.sattler22.intraday.service.IntradayTrackingService.Security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Intraday Security Unit Tests
 *
 * @author Pete Sattler
 * @version October 2025
 * @since February 12, 2019
 */
final class SecurityTest {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Test
    void testConstructorFailsWhenTradeDateIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Security(null, TestStockTickers.APPLE, BigDecimal.ONE));
    }

    @Test
    void testConstructorFailsWhenSymbolIsNull() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, null, BigDecimal.ONE));
    }

    @Test
    void testConstructorFailsWhenPriceIsNull() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            new Security(tradeDate, TestStockTickers.APPLE, null));
    }

    @Test
    void testConstructorFailsWhenPriceIsInvalid() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
            new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ZERO));
    }

    @Test
    void testConstructorHappyPath() {
        final String expectedSymbol = TestStockTickers.APPLE;
        final BigDecimal expectedPrice = new BigDecimal("178.44");
        final Security security = new Security(LocalDate.now(), expectedSymbol, expectedPrice);
        assertImpl(security, expectedSymbol, expectedPrice, expectedPrice, expectedPrice);
    }

    @Test
    void testUpdateOnePriceHappyPath() {
        final String expectedSymbol = TestStockTickers.FACEBOOK;
        final BigDecimal nbrPrices = new BigDecimal("2");
        final BigDecimal lowPrice = new BigDecimal("184.19");
        final BigDecimal highPrice = new BigDecimal("196.50");
        final BigDecimal expectedAverage = lowPrice.add(highPrice).divide(nbrPrices, ROUNDING_MODE);
        final Security security = new Security(LocalDate.now(), expectedSymbol, lowPrice);
        security.update(highPrice);
        assertImpl(security, expectedSymbol, lowPrice, highPrice, expectedAverage);
    }

    @Test
    void testUpdateTwoPricesHappyPath() {
        final String expectedSymbol = TestStockTickers.FACEBOOK;
        final BigDecimal nbrPrices = new BigDecimal("3");
        final BigDecimal initialPrice = new BigDecimal("184.19");
        final BigDecimal highPrice = new BigDecimal("196.50");
        final BigDecimal lowPrice = new BigDecimal("178.25");
        final BigDecimal expectedAverage = (initialPrice.add(lowPrice).add(highPrice)).divide(nbrPrices, ROUNDING_MODE);
        final Security security = new Security(LocalDate.now(), expectedSymbol, initialPrice);
        security.update(highPrice);
        security.update(lowPrice);
        assertImpl(security, expectedSymbol, lowPrice, highPrice, expectedAverage);
    }

    /**
     * Test standard assertions
     */
    private static void assertImpl(Security actual, String symbol, BigDecimal lowPrice,
                                   BigDecimal highPrice, BigDecimal avgPrice) {
        assertEquals(symbol, actual.symbol());
        assertEquals(lowPrice, actual.lowPrice());
        assertEquals(highPrice, actual.highPrice());
        assertEquals(avgPrice, actual.calcAveragePrice(ROUNDING_MODE));
    }
}
