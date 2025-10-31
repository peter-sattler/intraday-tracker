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
 * @version October 2025
 * @since February 12, 2019
 */
final class IntradayTrackingServiceTest {

    private IntradayTrackingService intradayTrackingService;

    @BeforeEach
    void setUp() {
        this.intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
    }

    @Test
    void testGetSecurityFailsWhenSymbolIsNull() {
        assertThrows(NullPointerException.class, () ->
            intradayTrackingService.security(null));
    }

    @Test
    void testGetSecurityFailsWhenSymbolIsNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            intradayTrackingService.security(TestStockTickers.GOOGLE);  //They are searching for answers ;)
        });
    }

    @Test
    void testBookTradeFailsWhenSymbolIsNull() {
        final LocalDate tradeDate = LocalDate.now();
        assertThrows(NullPointerException.class, () ->
            intradayTrackingService.book(tradeDate, null, BigDecimal.ONE));
    }

    @Test
    void testBookTradeAndGetSymbolHappyPath() {
        final String symbol = TestStockTickers.APPLE;
        final BigDecimal price = new BigDecimal("178.44");
        final Security expected = new Security(LocalDate.now(), symbol, price);
        intradayTrackingService.book(LocalDate.now(), symbol, price);
        final Security actual = intradayTrackingService.security(TestStockTickers.APPLE);
        assertEquals(expected, actual);
    }

    @Test
    void testBookTradeAndGetSecuritiesHappyPath1() {
        intradayTrackingService.book(LocalDate.now(), TestStockTickers.APPLE, new BigDecimal("178.44"));
        assertEquals(1, intradayTrackingService.securities().size());
    }

    @Test
    void testBookTradeAndGetSecuritiesHappyPath2() {
        final LocalDate tradeDate = LocalDate.now();
        intradayTrackingService.book(tradeDate, TestStockTickers.APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(tradeDate, TestStockTickers.APPLE, new BigDecimal("163.84"));
        assertEquals(1, intradayTrackingService.securities().size());
    }

    @Test
    void testBookTradeAndGetSecuritiesMapHappyPath3() {
        final LocalDate tradeDate = LocalDate.now();
        intradayTrackingService.book(tradeDate, TestStockTickers.APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(tradeDate.minusDays(1L), TestStockTickers.GOOGLE, new BigDecimal("1149.49"));
        assertEquals(2, intradayTrackingService.securities().size());
    }
}
