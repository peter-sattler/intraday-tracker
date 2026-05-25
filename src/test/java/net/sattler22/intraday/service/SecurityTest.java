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
                new Security(null, TestStockTickers.APPLE, BigDecimal.ONE, BigDecimal.ONE, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenSymbolIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, null, BigDecimal.ONE, BigDecimal.ONE, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenLowPriceIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            new Security(tradeDate, TestStockTickers.APPLE, null, BigDecimal.ONE, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenLowPriceIsZero_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
            new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ZERO, BigDecimal.ONE, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenLowPriceExceedsHighPrice_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.INTL_BUSINESS_MACHINES, BigDecimal.TEN, BigDecimal.ONE, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenHighPriceIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, TestStockTickers.APPLE, BigDecimal.ONE, null, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenHighPriceIsZero_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ONE, BigDecimal.ZERO, 1L, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenPriceCountIsNegative_thenThrowIllegalArgumentException() {
        newInstancePriceCountThrowsIllegalArgumentException(-1L);
    }

    @Test
    void newInstance_whenPriceCountIsZero_thenThrowIllegalArgumentException() {
        newInstancePriceCountThrowsIllegalArgumentException(0L);
    }

    private void newInstancePriceCountThrowsIllegalArgumentException(long priceCount) {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.AMERICAN_INTL_GROUP, BigDecimal.ONE, BigDecimal.ONE, priceCount, BigDecimal.ONE));
    }

    @Test
    void newInstance_whenPriceSumIsNull_thenThrowIllegalArgumentException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                new Security(tradeDate, TestStockTickers.GOOGLE, BigDecimal.ONE, BigDecimal.ONE, 1L, null));
    }

    @Test
    void newInstance_whenPriceSumIsNegative_thenThrowIllegalArgumentException() {
        newInstancePriceSumThrowsIllegalArgumentException(BigDecimal.valueOf(-1));
    }

    @Test
    void newInstance_whenPriceSumIsZero_thenThrowIllegalArgumentException() {
        newInstancePriceSumThrowsIllegalArgumentException(BigDecimal.ZERO);
    }

    private void newInstancePriceSumThrowsIllegalArgumentException(BigDecimal priceSum) {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Security(tradeDate, TestStockTickers.JPMORGAN_CHASE, BigDecimal.ONE, BigDecimal.ONE, 1L, priceSum));
    }

    @Test
    void calcAveragePrice_whenScaleIsNegative_thenThrowIllegalArgumentException() {
        final Security security = new Security(LocalDate.now(), TestStockTickers.APPLE, BigDecimal.ONE, BigDecimal.ONE, 1L, BigDecimal.ONE);
        assertThrows(IllegalArgumentException.class, () ->
                security.calcAveragePrice(-1, RoundingMode.CEILING));
    }

    @Test
    void newInstance_whenHappyPath_thenSuccessful() {
        final LocalDate expectedTradeDate = LocalDate.now();
        final String expectedSymbol = TestStockTickers.APPLE;
        final BigDecimal expectedPrice = new BigDecimal("178.44");
        final long expectedPriceCount = 1L;
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
