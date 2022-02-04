package net.sattler22.intraday.service;

import static net.sattler22.intraday.TestConstants.APPLE;
import static net.sattler22.intraday.TestConstants.GOOGLE;
import static net.sattler22.intraday.TestConstants.TRADE_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    void testGetSymbolFailsNullSymbol() {
        assertThrows(NullPointerException.class, () -> {
            intradayTrackingService.security(null);
        });
    }

    @Test
    void testGetSymbolFailsSymbolNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            intradayTrackingService.security(APPLE);
        });
    }

    @Test
    void testRecordFailsNullSymbol() {
        assertThrows(NullPointerException.class, () -> {
            final var price = BigDecimal.ONE;
            intradayTrackingService.book(TRADE_DATE, null, price);
        });
    }

    @Test
    void testRecordAndGetSymbolHappyPath() {
        final var tradeDate = LocalDate.now();
        final var symbol = APPLE;
        final var price = new BigDecimal("178.44");
        final var expected = new IntradaySecurityImpl(tradeDate, symbol, price);
        intradayTrackingService.book(tradeDate, symbol, price);
        final var actual = intradayTrackingService.security(APPLE);
        assertEquals(expected, actual);
    }

    @Test
    void testRecordAndGetSecuritiesHappyPath1() {
        final var expectedSize = 1;
        intradayTrackingService.book(LocalDate.now(), APPLE, new BigDecimal("178.44"));
        assertEquals(expectedSize, intradayTrackingService.securities().size());
    }

    @Test
    void testRecordAndGetSecuritiesHappyPath2() {
        final var tradeDate = LocalDate.now();
        final var expectedSize = 1;
        intradayTrackingService.book(tradeDate, APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(tradeDate, APPLE, new BigDecimal("163.84"));
        assertEquals(expectedSize, intradayTrackingService.securities().size());
    }

    @Test
    void testRecordAndGetSecuritiesMapHappyPath3() {
        final var tradeDate = LocalDate.now();
        final var expectedSize = 2;
        intradayTrackingService.book(tradeDate, APPLE, new BigDecimal("178.44"));
        intradayTrackingService.book(tradeDate.minusDays(1L), GOOGLE, new BigDecimal("1149.49"));
        assertEquals(expectedSize, intradayTrackingService.securities().size());
    }
}
