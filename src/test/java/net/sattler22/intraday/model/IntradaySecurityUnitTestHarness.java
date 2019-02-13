package net.sattler22.intraday.model;

import static net.sattler22.intraday.TestConstants.APPLE;
import static net.sattler22.intraday.TestConstants.FACEBOOK;
import static net.sattler22.intraday.TestConstants.GOOGLE;
import static net.sattler22.intraday.TestConstants.ROUNDING_MODE;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import net.sattler22.intraday.service.IntradayTrackingService.Security;

/**
 * Intraday Security Unit Test Harness
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
public class IntradaySecurityUnitTestHarness {

    @Test(expected = NullPointerException.class)
    public void testConstructorFailsNullTradeDate() {
        new IntradaySecurityImpl(null, APPLE, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorFailsNullSymbol() {
        new IntradaySecurityImpl(LocalDate.now(), null, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorFailsNullPrice() {
        new IntradaySecurityImpl(LocalDate.now(), APPLE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidPrice() {
        new IntradaySecurityImpl(LocalDate.now(), GOOGLE, BigDecimal.ZERO);
    }

    @Test
    public void testConstructorHappyPath() {
        final LocalDate tradeDate = LocalDate.now();
        final String expectedSymbol = APPLE;
        final BigDecimal expectedPrice = new BigDecimal("178.44");
        final Security intradaySecurity = new IntradaySecurityImpl(tradeDate, expectedSymbol, expectedPrice);
        assertImpl(intradaySecurity, expectedSymbol, expectedPrice, expectedPrice, expectedPrice);
    }

    @Test
    public void testUpdateOnePriceHappyPath() {
        final LocalDate tradeDate = LocalDate.now();
        final String expectedSymbol = FACEBOOK;
        final BigDecimal nbrPrices = new BigDecimal("2");
        final BigDecimal lowPrice = new BigDecimal("184.19");
        final BigDecimal highPrice = new BigDecimal("196.50");
        final BigDecimal expectedAverage = lowPrice.add(highPrice).divide(nbrPrices, ROUNDING_MODE);
        final Security intradaySecurity = new IntradaySecurityImpl(tradeDate, expectedSymbol, lowPrice);
        intradaySecurity.update(tradeDate, highPrice);
        assertImpl(intradaySecurity, expectedSymbol, lowPrice, highPrice, expectedAverage);
    }

    @Test
    public void testUpdateTwoPricesHappyPath() {
        final LocalDate tradeDate = LocalDate.now();
        final String expectedSymbol = FACEBOOK;
        final BigDecimal nbrPrices = new BigDecimal("3");
        final BigDecimal initialPrice = new BigDecimal("184.19");
        final BigDecimal highPrice = new BigDecimal("196.50");
        final BigDecimal lowPrice = new BigDecimal("178.25");
        final BigDecimal expectedAverage = (initialPrice.add(lowPrice).add(highPrice)).divide(nbrPrices, ROUNDING_MODE);
        final Security intradaySecurity = new IntradaySecurityImpl(tradeDate, expectedSymbol, initialPrice);
        intradaySecurity.update(tradeDate, highPrice);
        intradaySecurity.update(tradeDate, lowPrice);
        assertImpl(intradaySecurity, expectedSymbol, lowPrice, highPrice, expectedAverage);
    }

    /**
     * Test standard assertions
     */
    private void assertImpl(Security actual, String symbol, BigDecimal lowPrice, BigDecimal highPrice, BigDecimal avgPrice) {
        assertEquals(symbol, actual.getSymbol());
        assertEquals(lowPrice, actual.getLowPrice());
        assertEquals(highPrice, actual.getHighPrice());
        assertEquals(avgPrice, actual.calcAveragePrice(ROUNDING_MODE));
    }
}
