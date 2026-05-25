package net.sattler22.intraday.client;

import net.sattler22.intraday.service.IntradayTrackingService;
import net.sattler22.intraday.service.IntradayTrackingServiceInMemoryImpl;

import java.io.Console;
import java.math.RoundingMode;

/**
 * Intraday Tracking Command-Line Client
 *
 * @author Pete Sattler
 * @since February 12, 2019
 * @version May 2026
 */
public final class IntradayTrackingCommandLineClient {

    private static final String USER_TERMINATE = "quit";

    private IntradayTrackingCommandLineClient() {
        throw new AssertionError("Class cannot be instantiated");
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
        console.printf("*** Intraday Tracker Command-Line Client ***%n");
        final IntradayTrackingService intradayTrackingService = new IntradayTrackingServiceInMemoryImpl();
        while (true) {
            console.printf("Enter {TRADE DATE (YYYY-MM-DD)} {SYMBOL} {PRICE} or quit to terminate%n> ");
            final String userInput = console.readLine();
            if (userInput == null || USER_TERMINATE.equalsIgnoreCase(userInput.strip())) {
                console.printf("Intraday Tracker Command-Line Client terminated%n");
                break;
            }
            try {
                IntradayTrackingClientParser.parse(userInput).ifPresent(tradeData -> {
                    intradayTrackingService.book(tradeData.tradeDate(), tradeData.symbol(), tradeData.price());
                    intradayTrackingService.list().forEach(security ->
                        console.printf("< %s %s %s %s %s%n", security.tradeDate(), security.symbol(), security.highPrice(),
                                security.lowPrice(), security.calcAveragePrice(2, RoundingMode.HALF_UP)));
                });
            }
            catch (RuntimeException runtimeException) {
                runtimeException.printStackTrace(System.err);
            }
        }
    }
}
