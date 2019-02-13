package net.sattler22.intraday.service;

import static net.sattler22.intraday.TestConstants.APPLE;
import static net.sattler22.intraday.TestConstants.GOOGLE;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.intraday.model.IntradaySecurityImpl;
import net.sattler22.intraday.service.IntradayTrackingService.Security;

/**
 * Intraday Tracking Service Unit Test Harness
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
public class IntradayTrackingServiceUnitTestHarness {

    private IntradayTrackingService intradayTrackingService;

    @Before
    public void setUp() throws Exception {
        this.intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
    }

    @Test(expected = NullPointerException.class)
    public void testGetSymbolFailsNullSymbol() {
        intradayTrackingService.getSecurity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSymbolFailsSymbolNotFound() {
        intradayTrackingService.getSecurity(APPLE);
    }

    @Test(expected = NullPointerException.class)
    public void testRecordFailsNullSymbol() {
        intradayTrackingService.record(LocalDate.now(), null, BigDecimal.ONE);
    }

    @Test
    public void testRecordAndGetSymbolHappyPath() {
        final LocalDate tradeDate = LocalDate.now();
        final String symbol = APPLE;
        final BigDecimal price = new BigDecimal("178.44");
        final Security expected = new IntradaySecurityImpl(tradeDate, symbol, price);
        intradayTrackingService.record(tradeDate, symbol, price);
        final Security actual = intradayTrackingService.getSecurity(APPLE);
        assertEquals(expected, actual);
    }

    @Test
    public void testRecordAndGetSecuritiesHappyPath1() {
        final int expectedSize = 1;
        intradayTrackingService.record(LocalDate.now(), APPLE, new BigDecimal("178.44"));
        assertEquals(expectedSize, intradayTrackingService.getSecurities().size());
    }

    @Test
    public void testRecordAndGetSecuritiesHappyPath2() {
        final LocalDate tradeDate = LocalDate.now();
        final int expectedSize = 1;
        intradayTrackingService.record(tradeDate, APPLE, new BigDecimal("178.44"));
        intradayTrackingService.record(tradeDate, APPLE, new BigDecimal("163.84"));
        assertEquals(expectedSize, intradayTrackingService.getSecurities().size());
    }

    @Test
    public void testRecordAndGetSecuritiesMapHappyPath3() {
        final LocalDate tradeDate = LocalDate.now();
        final int expectedSize = 2;
        intradayTrackingService.record(tradeDate, APPLE, new BigDecimal("178.44"));
        intradayTrackingService.record(tradeDate.minusDays(1L), GOOGLE, new BigDecimal("1149.49"));
        assertEquals(expectedSize, intradayTrackingService.getSecurities().size());
    }
}
