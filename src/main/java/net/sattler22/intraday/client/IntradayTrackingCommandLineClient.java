package net.sattler22.intraday.client;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

import net.sattler22.intraday.service.IntradayTrackingService;
import net.sattler22.intraday.service.IntradayTrackingService.Security;
import net.sattler22.intraday.service.IntradayTrackingServiceInMemoryImpl;

/**
 * Intraday Tracking Command Line Client
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
public final class IntradayTrackingCommandLineClient {

    private static final int USER_PROMPT_NBR_FIELDS = 3;
    private static final Pattern USER_PROMPT_PATTERN = Pattern.compile(" \\s*");
    private static final String USER_PROMPT = "Enter {TRADE DATE (YYYY-MM-DD)} {SYMBOL} {PRICE} or quit to terminate";
    private static final String USER_TERMINATE = "quit";
    private final IntradayTrackingService service;

    /**
     * Constructs a new intraday tracking command-line client
     *
     * @param service The intraday tracking service implementation (dependency injection)
     */
    public IntradayTrackingCommandLineClient(IntradayTrackingService service) {
        this.service = service;
    }

    /**
     * @see net.sattler22.intraday.service.IntradayTrackingService#record(LocalDate, String, BigDecimal)
     */
    public void record(LocalDate tradeDate, String symbol, BigDecimal price) {
        service.record(tradeDate, symbol, price);
    }

    /**
     * @see net.sattler22.intraday.service.IntradayTrackingService#getSecurities()
     */
    public Collection<Security> getSecurities() {
        return service.getSecurities();
    }

    /**
     * Executes the command-line client
     */
    public static void main(String[] args) {
        final IntradayTrackingService service = new IntradayTrackingServiceInMemoryImpl();
        final IntradayTrackingCommandLineClient client = new IntradayTrackingCommandLineClient(service);
        final Scanner scanner = new Scanner(System.in);
        System.out.println("Intraday Tracking Command-Line Client");
        try {
            while (true) {
                // Get user input:
                System.out.println();
                System.out.println(USER_PROMPT);
                String input = scanner.nextLine();
                if (USER_TERMINATE.equals(input))
                    break;
                final String[] splitInput = USER_PROMPT_PATTERN.split(input);
                if (splitInput.length != USER_PROMPT_NBR_FIELDS) {
                    System.out.println(USER_PROMPT);
                    continue;
                }
                // Record price:
                LocalDate tradeDate = LocalDate.parse(splitInput[0]);
                if (tradeDate == null) {
                    System.out.println(USER_PROMPT);
                    continue;
                }
                String symbol = splitInput[1];
                BigDecimal price = new BigDecimal(splitInput[2]);
                client.record(tradeDate, symbol, price);

                // Report results:
                System.out.format("> %s\n", input);
                for (Security security : client.getSecurities())
                    System.out.format("< %s %s %s %s %s\n", security.getTradeDate(), security.getSymbol(), security.getHighPrice(),
                            security.getLowPrice(), security.calcAveragePrice(HALF_UP));
            }
            System.out.println();
            System.out.println("Intraday Tracking Command-Line Client terminated");
        }
        catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
        finally {
            scanner.close();
        }
    }
}
