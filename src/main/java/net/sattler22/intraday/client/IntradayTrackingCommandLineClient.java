package net.sattler22.intraday.client;

import net.sattler22.intraday.service.IntradayTrackingService;
import net.sattler22.intraday.service.IntradayTrackingServiceInMemoryImpl;

import java.io.Console;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;

/**
 * Intraday Tracking Command-Line Client
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
public record IntradayTrackingCommandLineClient(IntradayTrackingService intradayTrackingService) {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final String USER_PROMPT = "Enter {TRADE DATE (YYYY-MM-DD)} {SYMBOL} {PRICE} or quit to terminate";
    private static final String USER_TERMINATE = "quit";

    public IntradayTrackingCommandLineClient {
        Objects.requireNonNull(intradayTrackingService, "Intraday tracking service is required");
    }

    private void book(IntradayTrackingClientParser.TradeData tradeData) {
        intradayTrackingService.book(tradeData.tradeDate(), tradeData.symbol(), tradeData.price());
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
        console.printf("*** Intraday Tracker Command-Line Client (Rounding mode: [%s]) ***%n", ROUNDING_MODE.name());
        final IntradayTrackingService intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
        final IntradayTrackingCommandLineClient intradayTrackingClient =
                new IntradayTrackingCommandLineClient(intradayTrackingService);
        while (true) {
            console.printf("%s%n> ", USER_PROMPT);
            final String userInput = console.readLine();
            if (userInput == null || USER_TERMINATE.equalsIgnoreCase(userInput.strip())) {
                console.printf("Intraday Tracker Command-Line Client terminated%n");
                break;
            }
            try {
                IntradayTrackingClientParser.parse(userInput).ifPresent(tradeData -> {
                    intradayTrackingClient.book(tradeData);
                    displayResults(console, intradayTrackingClient.securities());
                });
            }
            catch (RuntimeException runtimeException) {
                runtimeException.printStackTrace(System.err);
            }
        }
    }

    private static void displayResults(Console console, Collection<IntradayTrackingService.Security> securities) {
        for (final IntradayTrackingService.Security security : securities) {
            console.printf("< %s %s %s %s %s%n", security.tradeDate(), security.symbol(), security.highPrice(),
                    security.lowPrice(), security.calcAveragePrice(2, ROUNDING_MODE));
        }
    }
}
