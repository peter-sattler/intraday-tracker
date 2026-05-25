package net.sattler22.intraday.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static net.sattler22.intraday.service.IntradayTrackingService.Security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intraday Tracking Service In Memory Unit Tests
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
final class IntradayTrackingServiceInMemoryImplTest {

    private IntradayTrackingService intradayTrackingService;

    @BeforeEach
    void setUp() {
        this.intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
    }

    @Test
    void find_whenSymbolIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
            intradayTrackingService.find(null));
    }

    @Test
    void find_whenSymbolIsBlank_thenThrowIllegalArgumentException() {
        findSymbolThrowsIllegalArgumentException(" ".repeat(8));
    }

    @Test
    void find_whenSymbolIsEmpty_thenThrowIllegalArgumentException() {
        findSymbolThrowsIllegalArgumentException("");
    }

    private void findSymbolThrowsIllegalArgumentException(String symbol) {
        assertThrows(IllegalArgumentException.class, () ->
                intradayTrackingService.find(symbol));
    }

    @Test
    void find_whenSymbolIsNotFound_thenReturnEmptyOptional() {
        assertTrue(intradayTrackingService.find(TestStockTickers.JPMORGAN_CHASE).isEmpty());
    }

    @Test
    void find_whenSymbolFound_thenSuccessful() {
        final LocalDate expectedTradeDate = LocalDate.now();
        final String expectedSymbol = TestStockTickers.FACEBOOK;
        final BigDecimal expectedPrice = BigDecimal.TEN;
        intradayTrackingService.book(expectedTradeDate, expectedSymbol, expectedPrice);
        final Security actual = intradayTrackingService.find(expectedSymbol)
                .orElseThrow(() -> new NoSuchElementException("Symbol %s not found".formatted(expectedSymbol)));
        assertEquals(expectedTradeDate, actual.tradeDate());
        assertEquals(expectedSymbol, actual.symbol());
        assertEquals(expectedPrice, actual.lowPrice());
        assertEquals(expectedPrice, actual.highPrice());
        assertEquals(1L, actual.priceCount());
        assertEquals(expectedPrice, actual.priceSum());
    }

    @Test
    void list_withOneSecurityOnePrice_thenSuccessful() {
        intradayTrackingService.book(LocalDate.now(), TestStockTickers.INTL_BUSINESS_MACHINES, new BigDecimal("100.00"));
        assertEquals(1, intradayTrackingService.list().size());
    }

    @Test
    void list_withOneSecurityTwoPricesSameTradeDate_thenSuccessful() {
        final LocalDate tradeDate = LocalDate.now();
        intradayTrackingService.book(tradeDate, TestStockTickers.GOOGLE, new BigDecimal("1149.49"));
        intradayTrackingService.book(tradeDate, TestStockTickers.GOOGLE, new BigDecimal("1148.10"));
        assertEquals(1, intradayTrackingService.list().size());
    }

    @Test
    void list_withTwoSecuritiesDifferentTradeDates_thenSuccessful() {
        final LocalDate tradeDate = LocalDate.now();
        intradayTrackingService.book(tradeDate.minusDays(1L), TestStockTickers.GOOGLE, new BigDecimal("1149.49"));
        intradayTrackingService.book(tradeDate, TestStockTickers.FACEBOOK, new BigDecimal("184.19"));
        assertEquals(2, intradayTrackingService.list().size());
    }

    @Test
    void book_whenTradeDateIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                intradayTrackingService.book(null, TestStockTickers.GOOGLE, BigDecimal.ONE));
    }

    @Test
    void book_whenSymbolIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            intradayTrackingService.book(tradeDate, null, BigDecimal.TEN));
    }

    @Test
    void book_whenSymbolIsBlank_thenThrowIllegalArgumentException() {
        bookSymbolThrowsIllegalArgumentException(" ".repeat(8));
    }

    @Test
    void book_whenSymbolIsEmpty_thenThrowIllegalArgumentException() {
        bookSymbolThrowsIllegalArgumentException("");
    }

    private void bookSymbolThrowsIllegalArgumentException(String symbol) {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                intradayTrackingService.book(tradeDate, symbol, BigDecimal.TWO));
    }

    @Test
    void book_whenPriceIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
                intradayTrackingService.book(tradeDate, TestStockTickers.INTL_BUSINESS_MACHINES, null));
    }

    @Test
    void book_whenPriceIsNegative_thenThrowIllegalArgumentException() {
        bookPriceThrowsIllegalArgumentException(BigDecimal.valueOf(-1));
    }

    @Test
    void book_whenPriceIsZero_thenThrowIllegalArgumentException() {
        bookPriceThrowsIllegalArgumentException(BigDecimal.ZERO);
    }

    private void bookPriceThrowsIllegalArgumentException(BigDecimal price) {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                intradayTrackingService.book(tradeDate, TestStockTickers.AMERICAN_INTL_GROUP, price));
    }

    @Test
    void book_withOnePrice_thenSuccessful() {
        final String symbol = TestStockTickers.APPLE;
        final LocalDate tradeDate = LocalDate.now();
        final BigDecimal price = new BigDecimal("178.44");
        final Security expected = new Security(tradeDate, symbol, price, price, 1L, price);
        intradayTrackingService.book(tradeDate, symbol, price);
        final Security actual = intradayTrackingService.find(TestStockTickers.APPLE)
                .orElseThrow(() -> new NoSuchElementException("Symbol %s not found".formatted(symbol)));
        assertEquals(expected, actual);
        assertEquals(price, actual.calcAveragePrice(2, RoundingMode.HALF_UP));
    }

    @Test
    void book_withTwoPrices_thenSuccessful() {
        final String symbol = TestStockTickers.APPLE;
        final LocalDate tradeDate = LocalDate.now();
        final List<BigDecimal> prices = List.of(new BigDecimal("178.44"), new BigDecimal("178.50"));
        final BigDecimal priceSum = prices.getFirst().add(prices.getLast());
        final BigDecimal expectedAverage = priceSum.divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP);
        intradayTrackingService.book(tradeDate, symbol, prices.getFirst());
        intradayTrackingService.book(tradeDate, symbol, prices.getLast());
        final Security expected =
                new Security(tradeDate, symbol, prices.getFirst(), prices.getLast(), prices.size(), priceSum);
        final Security actual = intradayTrackingService.find(TestStockTickers.APPLE)
                .orElseThrow(() -> new NoSuchElementException("Symbol %s not found".formatted(symbol)));
        assertEquals(expected, actual);
        assertEquals(expectedAverage, actual.calcAveragePrice(2, RoundingMode.HALF_UP));
    }
}
