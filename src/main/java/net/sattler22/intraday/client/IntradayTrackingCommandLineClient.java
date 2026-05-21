package net.sattler22.intraday.client;

import net.sattler22.intraday.service.IntradayTrackingService;
import net.sattler22.intraday.service.IntradayTrackingServiceInMemoryImpl;

import java.io.Console;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Intraday Tracking Command-Line Client
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
public record IntradayTrackingCommandLineClient(IntradayTrackingService intradayTrackingService) {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int USER_PROMPT_NBR_FIELDS = 3;
    private static final Pattern USER_PROMPT_PATTERN = Pattern.compile("\\s+");
    private static final String USER_PROMPT = "Enter {TRADE DATE (YYYY-MM-DD)} {SYMBOL} {PRICE} or quit to terminate";
    private static final String USER_TERMINATE = "quit";

    public IntradayTrackingCommandLineClient {
        Objects.requireNonNull(intradayTrackingService, "Intraday tracking service is required");
    }

    private void book(LocalDate tradeDate, String symbol, BigDecimal price) {
        intradayTrackingService.book(tradeDate, symbol, price);
    }

    private Collection<IntradayTrackingService.Security> securities() {
        return intradayTrackingService.securities();
    }

    /**
     * Executes the intraday tracking command-line client
     */
    static void main() {
        final Console console = System.console();
        if (console == null || !console.isTerminal()) {
            System.err.println("Please run from a terminal/command prompt");
            return;
        }
        console.printf("Intraday Tracker Command-Line Client%n");
        console.printf("Rounding Mode: [%s]%n%n", ROUNDING_MODE.name());
        final IntradayTrackingService intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
        final IntradayTrackingCommandLineClient commandLineClient =
                new IntradayTrackingCommandLineClient(intradayTrackingService);
        while (true) {
            console.printf("%s%n> ", USER_PROMPT);
            final String input = console.readLine();
            if (input == null || USER_TERMINATE.equalsIgnoreCase(input.strip())) {
                console.printf("Intraday Tracker Command-Line Client terminated%n");
                break;
            }
            try {
                final String[] splitInput = USER_PROMPT_PATTERN.split(input.strip());
                if (splitInput.length == USER_PROMPT_NBR_FIELDS) {
                    final LocalDate tradeDate = LocalDate.parse(splitInput[0]);
                    final String symbol = splitInput[1];
                    final BigDecimal price = new BigDecimal(splitInput[2]);
                    commandLineClient.book(tradeDate, symbol, price);
                    displayResults(console, commandLineClient.securities());
                }
            }
            catch (RuntimeException exception) {
                exception.printStackTrace(System.err);
            }
        }
    }

    private static void displayResults(Console console, Collection<IntradayTrackingService.Security> securities) {
        for (final IntradayTrackingService.Security security : securities) {
            console.printf("< %s %s %s %s %s%n", security.tradeDate(), security.symbol(), security.highPrice(),
                    security.lowPrice(), security.calcAveragePrice(ROUNDING_MODE));
        }
    }
}
