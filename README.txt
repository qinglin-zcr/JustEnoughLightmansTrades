Just Enough Lightman's Trades
=============================

A JEI addon for Lightman's Currency.

This mod adds trades from Lightman's Currency Persistent Traders to JEI,
allowing players to browse system shop trades directly through JEI's recipe
and usage interfaces.

Features
--------
- Displays Persistent Trader trades in JEI
- Displays each coin denomination separately, including change from price fluctuation rules
- Refreshes displayed prices using synced trader data and the current player's trade context
- Allows viewing shop trades through JEI recipe and usage lookups

Price display
-------------
Once a world is loaded and trader data has synced, displayed prices include Lightman's
Currency trade rules such as price fluctuations and player discounts. JEI refreshes
these displays as its displayed ingredients update. Price calculations are cached until
synced rules or trade data change, a fluctuation/sale/free-sample expiry is reached, or an
inventory change occurs for a shop with discount-code rules. A client-only Mixin observes
LC's UpdateTrader packets after loading, invalidating only the affected trader's quotes.
This covers synced stock changes for demand pricing, player discount settings and
free-sample claims. Ordinary inventory changes do not invalidate quotes for shops
without discount codes. There is no periodic price-calculation fallback.
Unknown third-party rules are cached until a known input changes; prices depending on
external unsynced state or unrecognized timers can remain stale. Manually entered
discount codes belong to LC's open shop menu and are not included in JEI's quote context.
Before trader data is available,
the configured base price is shown. Recipe lookups use the configured base ingredients;
dynamic price overrides affect the display rather than JEI's recipe index.

Requirements
------------
- Minecraft 1.20.1
- Forge 47.x
- JEI 15.20.x
- Lightman's Currency 2.3.x
