package net.sattler22.intraday.service;

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

    @Test
    void newInstance_whenTradeDateIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Security(null, TestStockTickers.APPLE, BigDecimal.ONE, BigDecimal.ONE, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenSymbolIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, null, BigDecimal.ONE, BigDecimal.ONE, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenLowPriceIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            new Security(tradeDate, TestStockTickers.APPLE, null, BigDecimal.ONE, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenLowPriceIsZero_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
            new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ZERO, BigDecimal.ONE, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenHighPriceIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, TestStockTickers.APPLE, BigDecimal.ONE, null, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenHighPriceIsZero_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ONE, BigDecimal.ZERO, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenLowPriceExceedsHighPrice_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.INTL_BUSINESS_MACHINES, BigDecimal.TEN, BigDecimal.ONE, 1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenPriceCountIsNegative_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ONE, BigDecimal.ONE, -1, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenPriceCountIsZero_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ONE, BigDecimal.ONE, 0, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenPriceSumIsNull_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ONE, BigDecimal.ONE, 1, null));
    }

    @Test
    void newInstance_whenHappyPath_thenSuccessful() {
        final LocalDate expectedTradeDate = LocalDate.now();
        final String expectedSymbol = TestStockTickers.APPLE;
        final BigDecimal expectedPrice = new BigDecimal("178.44");
        final int expectedPriceCount = 1;
        final Security actual =
                new Security(expectedTradeDate, expectedSymbol, expectedPrice, expectedPrice, expectedPriceCount, expectedPrice);
        assertEquals(expectedSymbol, actual.symbol());
        assertEquals(expectedPrice, actual.lowPrice());
        assertEquals(expectedPrice, actual.highPrice());
        assertEquals(expectedPriceCount, actual.priceCount());
        assertEquals(expectedPrice, actual.priceSum());
        assertEquals(expectedPrice, actual.calcAveragePrice(2, RoundingMode.HALF_UP));
    }
}
