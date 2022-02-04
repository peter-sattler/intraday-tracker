package net.sattler22.intraday.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.Immutable;
import net.sattler22.intraday.service.IntradayTrackingService;
import net.sattler22.intraday.service.IntradayTrackingService.Security;
import net.sattler22.intraday.service.IntradayTrackingServiceInMemoryImpl;

/**
 * Intraday Tracking Command Line Client
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
@Immutable
public final class IntradayTrackingCommandLineClient {

    private static final Logger logger = LoggerFactory.getLogger(IntradayTrackingCommandLineClient.class);
    private static final int USER_PROMPT_NBR_FIELDS = 3;
    private static final Pattern USER_PROMPT_PATTERN = Pattern.compile(" \\s*");
    private static final String USER_PROMPT = "Enter {TRADE DATE (YYYY-MM-DD)} {SYMBOL} {PRICE} or quit to terminate";
    private static final String USER_TERMINATE = "quit";
    private final IntradayTrackingService service;

    private IntradayTrackingCommandLineClient(IntradayTrackingService service) {
        this.service = service;               //Dependency injection
    }

    private void book(LocalDate tradeDate, String symbol, BigDecimal price) {
        service.book(tradeDate, symbol, price);
    }

    private Collection<Security> securities() {
        return service.securities();
    }

    /**
     * Executes the intraday tracking command-line client
     */
    public static void main(String[] args) {
        logger.info("Intraday Tracker Command-Line Client");
        final var service = new IntradayTrackingServiceInMemoryImpl();
        final var client = new IntradayTrackingCommandLineClient(service);
        final var roundingMode = RoundingMode.HALF_UP;
        if (logger.isInfoEnabled())
            logger.info("Rounding Mode: [{}]", roundingMode.name());
        try (final var scanner = new Scanner(System.in)) {
            while (true) {
                logger.info(USER_PROMPT);
                final var input = scanner.nextLine();
                if (USER_TERMINATE.equalsIgnoreCase(input.strip()))
                    break;
                final var splitInput = USER_PROMPT_PATTERN.split(input);
                if (splitInput.length == USER_PROMPT_NBR_FIELDS) {
                    final var tradeDate = LocalDate.parse(splitInput[0]);
                    if (tradeDate != null) {
                        final var symbol = splitInput[1];
                        final var price = new BigDecimal(splitInput[2]);
                        client.book(tradeDate, symbol, price);
                        logger.info("> {}", input);
                        displayResults(client.securities(), roundingMode);
                    }
                    else
                        logger.info(USER_PROMPT);
                }
            }
            logger.info("Intraday Tracker Command-Line Client terminated");
        }
        catch(RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void displayResults(Collection<Security> securities, RoundingMode roundingMode) {
        for (final var security : securities)
            if (logger.isInfoEnabled())
                logger.info("< {} {} {} {} {}",
                    security.tradeDate(), security.symbol(), security.highPrice(), security.lowPrice(), security.calcAveragePrice(roundingMode));
    }
}
