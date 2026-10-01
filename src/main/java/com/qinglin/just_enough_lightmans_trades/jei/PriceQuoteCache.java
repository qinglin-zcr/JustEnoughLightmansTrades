package com.qinglin.just_enough_lightmans_trades.jei;

import java.util.Objects;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/** A quote remains valid until its inputs change or a price-rule time boundary is reached. */
final class PriceQuoteCache<V> {
    private Object state;
    private V value;
    private long quotedAt;
    private long validUntil;
    private boolean populated;

    V get(Object state, long now, LongSupplier deadline, Supplier<V> calculate) {
        if(populated && Objects.equals(this.state, state) && now >= quotedAt && now < validUntil)
            return value;
        V quote = calculate.get();
        this.state = state;
        this.value = quote;
        this.quotedAt = now;
        this.validUntil = deadline.getAsLong();
        this.populated = true;
        return quote;
    }

    static long nextBoundary(long now, long duration) {
        if(duration <= 0)
            return Long.MAX_VALUE;
        long remaining = duration - Math.floorMod(now, duration);
        return now > Long.MAX_VALUE - remaining ? Long.MAX_VALUE : now + remaining;
    }

    /** LC's compareTime includes timestamp + duration; expiry is one millisecond later. */
    static long nextExpiry(long[] timestamps, long duration, long now) {
        long next = Long.MAX_VALUE;
        if(duration <= 0)
            return next;
        for(long timestamp : timestamps) {
            long end = timestamp > Long.MAX_VALUE - duration ? Long.MAX_VALUE : timestamp + duration;
            long expiry = end == Long.MAX_VALUE ? end : end + 1;
            if(expiry > now)
                next = Math.min(next, expiry);
        }
        return next;
    }
}
