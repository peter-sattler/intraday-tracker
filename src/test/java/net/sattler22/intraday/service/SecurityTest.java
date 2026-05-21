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
 * @since February 12, 2019
 * @version May 2026
 */
final class SecurityTest {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Test
    void newInstance_whenTradeDateIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Security(null, TestStockTickers.APPLE, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenSymbolIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, null, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenPriceIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            new Security(tradeDate, TestStockTickers.APPLE, null));
    }

    @Test
    void newInstance_whenPriceIsInvalid_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
            new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ZERO));
    }

    @Test
    void newInstance_whenHappyPath_thenSuccessful() {
        final String expectedSymbol = TestStockTickers.APPLE;
        final BigDecimal expectedPrice = new BigDecimal("178.44");
        final Security security = new Security(LocalDate.now(), expectedSymbol, expectedPrice);
        assertImpl(security, expectedSymbol, expectedPrice, expectedPrice, expectedPrice);
    }

    @Test
    void update_whenOnePrice_thenSuccessful() {
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
    void update_whenTwoPrices_thenSuccessful() {
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
