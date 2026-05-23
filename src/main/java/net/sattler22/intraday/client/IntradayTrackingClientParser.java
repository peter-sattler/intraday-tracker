package net.sattler22.intraday.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Intraday Tracking Client User Input Parser
 *
 * @author Pete Sattler
 * @since May 2026
 * @version May 2026
 */
final class IntradayTrackingClientParser {

    private static final int NBR_FIELDS = 3;
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

    private IntradayTrackingClientParser() {
        throw new AssertionError("Class cannot be instantiated");
    }

    /**
     * Trade data (parsed results)
     *
     * @param tradeDate The date the security was traded on
     * @param symbol The security's symbol (case-insensitive)
     * @param price The current price
     */
    record TradeData(LocalDate tradeDate, String symbol, BigDecimal price) {
    }

    /**
     * Parse the user input
     *
     * @param userInput The intraday security user input
     * @return The parsed trade data
     */
    static Optional<TradeData> parse(String userInput) {
        if (userInput != null) {
            final String[] splitInput = SPLIT_PATTERN.split(userInput.strip());
            if (splitInput.length == NBR_FIELDS) {
                final LocalDate tradeDate = LocalDate.parse(splitInput[0]);
                final String symbol = splitInput[1].strip();
                final BigDecimal price = new BigDecimal(splitInput[2]);
                return Optional.of(new TradeData(tradeDate, symbol, price));
            }
        }
        return Optional.empty();
    }
}
