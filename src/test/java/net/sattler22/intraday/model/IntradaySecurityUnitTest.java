package net.sattler22.intraday.model;

import static net.sattler22.intraday.TestConstants.APPLE;
import static net.sattler22.intraday.TestConstants.FACEBOOK;
import static net.sattler22.intraday.TestConstants.GOOGLE;
import static net.sattler22.intraday.TestConstants.ROUNDING_MODE;
import static net.sattler22.intraday.TestConstants.TRADE_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import net.sattler22.intraday.service.IntradayTrackingService.Security;

/**
 * Intraday Security Unit Test Harness
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
final class IntradaySecurityUnitTest {

    @Test
    void testConstructorFailsNullTradeDate() {
        assertThrows(NullPointerException.class, () -> {
            new IntradaySecurityImpl(null, APPLE, BigDecimal.ONE);
        });
    }

    @Test
    void testConstructorFailsNullSymbol() {
        assertThrows(NullPointerException.class, () -> {
            new IntradaySecurityImpl(TRADE_DATE, null, BigDecimal.ONE);
        });
    }

    @Test
    void testConstructorFailsNullPrice() {
        assertThrows(NullPointerException.class, () -> {
            new IntradaySecurityImpl(TRADE_DATE, APPLE, null);
        });
    }

    @Test
    void testConstructorInvalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IntradaySecurityImpl(TRADE_DATE, GOOGLE, BigDecimal.ZERO);
        });
    }

    @Test
    void testConstructorHappyPath() {
        final var tradeDate = LocalDate.now();
        final var expectedSymbol = APPLE;
        final var expectedPrice = new BigDecimal("178.44");
        final var intradaySecurity = new IntradaySecurityImpl(tradeDate, expectedSymbol, expectedPrice);
        assertImpl(intradaySecurity, expectedSymbol, expectedPrice, expectedPrice, expectedPrice);
    }

    @Test
    void testUpdateOnePriceHappyPath() {
        final var tradeDate = LocalDate.now();
        final var expectedSymbol = FACEBOOK;
        final var nbrPrices = new BigDecimal("2");
        final var lowPrice = new BigDecimal("184.19");
        final var highPrice = new BigDecimal("196.50");
        final var expectedAverage = lowPrice.add(highPrice).divide(nbrPrices, ROUNDING_MODE);
        final var intradaySecurity = new IntradaySecurityImpl(tradeDate, expectedSymbol, lowPrice);
        intradaySecurity.update(tradeDate, highPrice);
        assertImpl(intradaySecurity, expectedSymbol, lowPrice, highPrice, expectedAverage);
    }

    @Test
    void testUpdateTwoPricesHappyPath() {
        final var tradeDate = LocalDate.now();
        final var expectedSymbol = FACEBOOK;
        final var nbrPrices = new BigDecimal("3");
        final var initialPrice = new BigDecimal("184.19");
        final var highPrice = new BigDecimal("196.50");
        final var lowPrice = new BigDecimal("178.25");
        final var expectedAverage = (initialPrice.add(lowPrice).add(highPrice)).divide(nbrPrices, ROUNDING_MODE);
        final var intradaySecurity = new IntradaySecurityImpl(tradeDate, expectedSymbol, initialPrice);
        intradaySecurity.update(tradeDate, highPrice);
        intradaySecurity.update(tradeDate, lowPrice);
        assertImpl(intradaySecurity, expectedSymbol, lowPrice, highPrice, expectedAverage);
    }

    /**
     * Test standard assertions
     */
    private static void assertImpl(Security actual, String symbol, BigDecimal lowPrice, BigDecimal highPrice, BigDecimal avgPrice) {
        assertEquals(symbol, actual.symbol());
        assertEquals(lowPrice, actual.lowPrice());
        assertEquals(highPrice, actual.highPrice());
        assertEquals(avgPrice, actual.calcAveragePrice(ROUNDING_MODE));
    }
}
