package net.sattler22.intraday.service;

import static net.sattler22.intraday.TestConstants.APPLE;
import static net.sattler22.intraday.TestConstants.GOOGLE;
import static net.sattler22.intraday.TestConstants.TRADE_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sattler22.intraday.model.IntradaySecurityImpl;

/**
 * Intraday Tracking Service Unit Test Harness
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
final class IntradayTrackingServiceUnitTest {

    private IntradayTrackingService intradayTrackingService;

    @BeforeEach
    void setUp() {
        this.intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
    }

    @Test
    void testGetSecurityFailsWhenSymbolIsNull() {
        assertThrows(NullPointerException.class, () -> {
            intradayTrackingService.security(null);
        });
    }

    @Test
    void testGetSecurityFailsWhenSymbolIsNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            intradayTrackingService.security(GOOGLE);  //They are searching for answers ;)
        });
    }

    @Test
    void testBookTradeFailsWhenSymbolIsNull() {
        assertThrows(NullPointerException.class, () -> {
            intradayTrackingService.book(TRADE_DATE, null, BigDecimal.ONE);
        });
    }

    @Test
    void testBookTradeAndGetSymbolHappyPath() {
        final var symbol = APPLE;
        final var price = new BigDecimal("178.44");
        final var expected = new IntradaySecurityImpl(TRADE_DATE, symbol, price);
        intradayTrackingService.book(TRADE_DATE, symbol, price);
        final var actual = intradayTrackingService.security(APPLE);
        assertEquals(expected, actual);
    }

    @Test
    void testBookTradeAndGetSecuritiesHappyPath1() {
        final var expectedSize = 1;
        intradayTrackingService.book(TRADE_DATE, APPLE, new BigDecimal("178.44"));
        assertEquals(expectedSize, intradayTrackingService.securities().size());
    }

    @Test
    void testBookTradeAndGetSecuritiesHappyPath2() {
        final var expectedSize = 1;
        intradayTrackingService.book(TRADE_DATE, APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(TRADE_DATE, APPLE, new BigDecimal("163.84"));
        assertEquals(expectedSize, intradayTrackingService.securities().size());
    }

    @Test
    void testBookTradeAndGetSecuritiesMapHappyPath3() {
        final var expectedSize = 2;
        intradayTrackingService.book(TRADE_DATE, APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(TRADE_DATE.minusDays(1L), GOOGLE, new BigDecimal("1149.49"));
        assertEquals(expectedSize, intradayTrackingService.securities().size());
    }
}
