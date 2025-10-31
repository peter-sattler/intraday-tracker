package net.sattler22.intraday.client;

import net.sattler22.intraday.service.IntradayTrackingService;
import net.sattler22.intraday.service.IntradayTrackingServiceInMemoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Intraday Tracking Command Line Client
 *
 * @author Pete Sattler
 * @version October 2025
 * @since February 12, 2019
 */
public record IntradayTrackingCommandLineClient(IntradayTrackingService service) {

    private static final Logger logger = LoggerFactory.getLogger(IntradayTrackingCommandLineClient.class);
    private static final int USER_PROMPT_NBR_FIELDS = 3;
    private static final Pattern USER_PROMPT_PATTERN = Pattern.compile(" \\s*");
    private static final String USER_PROMPT = "Enter {TRADE DATE (YYYY-MM-DD)} {SYMBOL} {PRICE} or quit to terminate";
    private static final String USER_TERMINATE = "quit";

    private void book(LocalDate tradeDate, String symbol, BigDecimal price) {
        service.book(tradeDate, symbol, price);
    }

    private Collection<IntradayTrackingService.Security> securities() {
        return service.securities();
    }

    /**
     * Executes the intraday tracking command-line client
     */
    public static void main(String[] args) {
        logger.info("Intraday Tracker Command-Line Client");
        final IntradayTrackingService service = new IntradayTrackingServiceInMemoryImpl();
        final IntradayTrackingCommandLineClient client = new IntradayTrackingCommandLineClient(service);
        final RoundingMode roundingMode = RoundingMode.HALF_UP;
        if (logger.isInfoEnabled())
            logger.info("Rounding Mode: [{}]", roundingMode.name());
        try (final Scanner scanner = new Scanner(System.in)) {
            while (true) {
                logger.info(USER_PROMPT);
                final String input = scanner.nextLine();
                if (USER_TERMINATE.equalsIgnoreCase(input.strip()))
                    break;
                final String[] splitInput = USER_PROMPT_PATTERN.split(input);
                if (splitInput.length == USER_PROMPT_NBR_FIELDS) {
                    final LocalDate tradeDate = LocalDate.parse(splitInput[0]);
                    final String symbol = splitInput[1];
                    final BigDecimal price = new BigDecimal(splitInput[2]);
                    client.book(tradeDate, symbol, price);
                    displayResults(client.securities(), roundingMode);
                }
            }
            logger.info("Intraday Tracker Command-Line Client terminated");
        }
        catch(RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void displayResults(Collection<IntradayTrackingService.Security> securities, RoundingMode roundingMode) {
        for (final IntradayTrackingService.Security security : securities)
            if (logger.isInfoEnabled())
                logger.info("< {} {} {} {} {}",
                    security.tradeDate(), security.symbol(), security.highPrice(), security.lowPrice(), security.calcAveragePrice(roundingMode));
    }
}
