package com.qinglin.just_enough_lightmans_trades.jei;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceQuoteCacheTest {
    @Test
    void repeatedDisplayUpdatesReuseQuoteUntilTheActualHourBoundary() {
        PriceQuoteCache<List<String>> cache = new PriceQuoteCache<>();
        AtomicInteger calculations = new AtomicInteger();
        AtomicInteger ruleChecks = new AtomicInteger();
        long hour = 3_600_000;
        for(long now = hour + 1000; now < 2 * hour; now += 1000) {
            long time = now;
            List<String> result = cache.get("same synced trade", now,
                    () -> {
                        ruleChecks.incrementAndGet();
                        return PriceQuoteCache.nextBoundary(time, hour);
                    }, () -> {
                        calculations.incrementAndGet();
                        return List.of("3 copper", "5 iron");
                    });
            assertEquals(List.of("3 copper", "5 iron"), result);
        }
        assertEquals(1, calculations.get());
        assertEquals(1, ruleChecks.get());
        List<String> nextHour = cache.get("same synced trade", 2 * hour,
                () -> 3 * hour, () -> {
                    calculations.incrementAndGet();
                    return List.of("4 copper");
                });
        assertEquals(List.of("4 copper"), nextHour);
        assertEquals(2, calculations.get());
    }

    @Test
    void rulesWithoutKnownDeadlinesStayCachedUntilSyncedInputsChange() {
        PriceQuoteCache<Integer> cache = new PriceQuoteCache<>();
        AtomicInteger calculations = new AtomicInteger();
        for(long now = 100; now < 100_000; now += 1000)
            assertEquals(1, cache.get(1L, now, () -> Long.MAX_VALUE, calculations::incrementAndGet));
        assertEquals(2, cache.get(2L, 100_000, () -> Long.MAX_VALUE, calculations::incrementAndGet));
    }

    @Test
    void syncedSettingsOrPlayerInputsInvalidateAnOtherwiseUnexpiredQuote() {
        PriceQuoteCache<Integer> cache = new PriceQuoteCache<>();
        assertEquals(40, cache.get("old rules", 100, () -> Long.MAX_VALUE, () -> 40));
        assertEquals(35, cache.get("new rules", 101, () -> Long.MAX_VALUE, () -> 35));
        assertEquals(30, cache.get("new discount code", 102, () -> Long.MAX_VALUE, () -> 30));
    }

    @Test
    void clockCorrectionBackwardsInvalidatesTheOldTimeBucket() {
        PriceQuoteCache<Integer> cache = new PriceQuoteCache<>();
        assertEquals(40, cache.get("same trade", 100, () -> 200, () -> 40));
        assertEquals(35, cache.get("same trade", 90, () -> 100, () -> 35));
    }

    @Test
    void boundaryIsAlignedToLightmansClockRatherThanTheLastJeiRefresh() {
        assertEquals(3_600_000, PriceQuoteCache.nextBoundary(3_599_999, 3_600_000));
        assertEquals(7_200_000, PriceQuoteCache.nextBoundary(3_600_000, 3_600_000));
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextBoundary(Long.MAX_VALUE - 1, 3_600_000));
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextBoundary(100, 0));
    }

    @Test
    void sampleOrSaleRemainsValidAtTheInclusiveEndAndExpiresOneMillisecondLater() {
        PriceQuoteCache<Integer> cache = new PriceQuoteCache<>();
        AtomicInteger calculations = new AtomicInteger();
        long[] claims = {100, 200};
        assertEquals(1, cache.get("same memory", 1099,
                () -> PriceQuoteCache.nextExpiry(claims, 1000, 1099), calculations::incrementAndGet));
        assertEquals(1, cache.get("same memory", 1100,
                () -> { throw new AssertionError("deadline must be cached"); }, calculations::incrementAndGet));
        assertEquals(2, cache.get("same memory", 1101,
                () -> PriceQuoteCache.nextExpiry(claims, 1000, 1101), calculations::incrementAndGet));
        assertEquals(1201, PriceQuoteCache.nextExpiry(claims, 1000, 1101));
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextExpiry(claims, 1000, 1201));
    }

    @Test
    void emptyPermanentOrOverflowingSampleMemoriesDoNotCauseRepeatedRecalculation() {
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextExpiry(new long[0], 1000, 100));
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextExpiry(new long[]{100}, 0, 100));
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextExpiry(new long[]{Long.MAX_VALUE - 10}, 100, 100));
        assertEquals(Long.MAX_VALUE, PriceQuoteCache.nextExpiry(new long[]{Long.MAX_VALUE - 100}, 100, 100));
    }
}
