package net.sattler22.intraday.service;

import net.sattler22.intraday.TestStockTickers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static net.sattler22.intraday.service.IntradayTrackingService.Security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Intraday Tracking Service Unit Tests
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
final class IntradayTrackingServiceTest {

    private IntradayTrackingService intradayTrackingService;

    @BeforeEach
    void setUp() {
        this.intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
    }

    @Test
    void security_whenSymbolIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
            intradayTrackingService.security(null));
    }

    @Test
    void security_whenSymbolIsNotFound_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            intradayTrackingService.security(TestStockTickers.GOOGLE);  //They are searching for answers ;)
        });
    }

    @Test
    void book_whenSymbolIsNull_thenThrowNullPointerException() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            intradayTrackingService.book(tradeDate, null, BigDecimal.ONE));
    }

    @Test
    void book_withHappyPath1_thenSuccessful() {
        final String symbol = TestStockTickers.APPLE;
        final BigDecimal price = new BigDecimal("178.44");
        final Security expected = new Security(LocalDate.now(), symbol, price);
        intradayTrackingService.book(LocalDate.now(), symbol, price);
        final Security actual = intradayTrackingService.security(TestStockTickers.APPLE);
        assertEquals(expected, actual);
    }

    @Test
    void book_withHappyPath2_thenSuccessful() {
        intradayTrackingService.book(LocalDate.now(), TestStockTickers.APPLE, new BigDecimal("178.44"));
        assertEquals(1, intradayTrackingService.securities().size());
    }

    @Test
    void book_withHappyPath3_thenSuccessful() {
        final LocalDate tradeDate = LocalDate.now();
        intradayTrackingService.book(tradeDate, TestStockTickers.APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(tradeDate, TestStockTickers.APPLE, new BigDecimal("163.84"));
        assertEquals(1, intradayTrackingService.securities().size());
    }

    @Test
    void book_withHappyPath4_thenSuccessful() {
        final LocalDate tradeDate = LocalDate.now();
        intradayTrackingService.book(tradeDate, TestStockTickers.APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(tradeDate.minusDays(1L), TestStockTickers.GOOGLE, new BigDecimal("1149.49"));
        assertEquals(2, intradayTrackingService.securities().size());
    }
}
