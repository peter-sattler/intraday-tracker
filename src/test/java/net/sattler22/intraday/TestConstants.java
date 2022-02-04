package net.sattler22.intraday;

import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Test Symbols and Constants
 *
 * @author Pete Sattler
 * @version February 6, 2019
 */
public final class TestConstants {

    /**
     * Apple security symbol
     */
    public static final String APPLE = "APPL";

    /**
     * Facebook security symbol
     */
    public static final String FACEBOOK = "FB";

    /**
     * Google security symbol
     */
    public static final String GOOGLE = "GOOG";

    /**
     * Rounding mode
     */
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Trade date
     */
    public static final LocalDate TRADE_DATE = LocalDate.now();
}
