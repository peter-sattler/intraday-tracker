package net.sattler22.intraday.model;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import net.jcip.annotations.ThreadSafe;
import net.sattler22.intraday.service.IntradayTrackingService.Security;

/**
 * Intraday Security Implementation
 *
 * @author Pete Sattler
 * @version February 12, 2019
 */
@ThreadSafe
public final class IntradaySecurityImpl implements Security {

    private final LocalDate tradeDate;
    private final String symbol;
    private volatile BigDecimal lowPrice;
    private volatile BigDecimal highPrice;
    private volatile BigDecimal priceSum;
    private final List<BigDecimal> prices = new LinkedList<>();
    private final Object lockObject = new Object();

    /**
     * Constructs a new intraday security
     *
     * @param tradeDate The date the security was traded on
     * @param symbol The security's symbol
     * @param price The current price
     */
    public IntradaySecurityImpl(LocalDate tradeDate, String symbol, BigDecimal price) {
        this.tradeDate = Objects.requireNonNull(tradeDate, "Trade date is required");
        this.symbol = Objects.requireNonNull(symbol, "Symbol is required");
        this.lowPrice = Objects.requireNonNull(price, "Price is required");
        if (price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Price must be greater than zero");
        this.highPrice = price;
        this.priceSum = price;
        this.prices.add(price);
    }

    @Override
    public LocalDate tradeDate() {
        return tradeDate;
    }

    @Override
    public String symbol() {
        return symbol;
    }

    @Override
    public BigDecimal lowPrice() {
        return lowPrice;
    }

    @Override
    public BigDecimal highPrice() {
        return highPrice;
    }

    @Override
    public BigDecimal calcAveragePrice(RoundingMode roundingMode) {
        if (roundingMode == null)
            roundingMode = HALF_UP;  //Per interface contract
        synchronized (lockObject) {
            return priceSum.divide(new BigDecimal(prices.size()), roundingMode);
        }
    }

    @Override
    public boolean update(LocalDate tradeDate, BigDecimal price) {
        Objects.requireNonNull(price, "Price is required");
        synchronized (lockObject) {
            if (price.compareTo(lowPrice) < 0)
                this.lowPrice = price;
            if (price.compareTo(highPrice) > 0)
                this.highPrice = price;
            this.priceSum = priceSum.add(price);
            return this.prices.add(price);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeDate, symbol);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final var that = (Security) other;
        return Objects.equals(this.tradeDate, that.tradeDate()) && Objects.equals(this.symbol, that.symbol());
    }

    @Override
    public String toString() {
        return String.format("%s [tradeDate=%s, symbol=%s, lowPrice=%s, highPrice=%s, priceSum=%s, prices=%s]",
                              getClass().getSimpleName(), tradeDate, symbol, lowPrice, highPrice, priceSum, prices);
    }
}
