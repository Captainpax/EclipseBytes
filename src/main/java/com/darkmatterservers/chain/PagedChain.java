package com.darkmatterservers.chain;

import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;

import java.util.*;

/**
 * A paged chain with uniform pages.
 * Use Builder to declare:
 * - chainId (title)
 * - pages (Page objects)
 * - handlers for buttons/dropdowns (by component id)
 * <p>
 * Runtime state (stored in ComponentContext):
 *  - Keys.PAGE_INDEX    -> int       (0-based)
 *  - Keys.TOTAL_PAGES   -> int
 *  - Keys.MESSAGE_ID    -> String    (the Discord message we edit in-place)
 *  - Keys.CHANNEL_ID    -> String    (where the message lives)
 *  - Keys.GUILD_ID      -> String    (optional, if relevant)
 */
public class PagedChain {

    /** Canonical context keys for consistent persistence and rendering. */
    public static final class Keys {
        public static final String PAGE_INDEX  = "pageIndex";
        public static final String TOTAL_PAGES = "totalPages";
        public static final String MESSAGE_ID  = "messageId";   // set by the runtime after the first send
        public static final String CHANNEL_ID  = "channelId";   // set by the runtime after the first send
        public static final String GUILD_ID    = "guildId";     // optional: set by chain logic
    }

    private final String chainId;
    private final List<Page> pages;

    private PagedChain(String chainId, List<Page> pages, Map<String, ComponentHandler> handlers) {
        if (pages.isEmpty()) throw new IllegalArgumentException("PagedChain requires at least one page");
        this.chainId = chainId;
        this.pages = List.copyOf(pages);
        Map<String, ComponentHandler> handlers1 = Map.copyOf(handlers);
        // Register all handlers with the global router
        handlers1.forEach(InteractionRouter::register);
    }

    public String chainId() {
        return chainId;
    }

    public int totalPages() {
        return pages.size();
    }

    public Page page(int index) {
        return pages.get(index);
    }

    public int clampIndex(int i) {
        if (i < 0) return 0;
        if (i >= pages.size()) return pages.size() - 1;
        return i;
    }

    // ---------------------------------
    // Navigation helpers (static)
    // ---------------------------------

    /** Read page index from context; returns 0 if missing/invalid. */
    public static int getPageIndex(ComponentContext ctx) {
        Object v = ctx.get(Keys.PAGE_INDEX);
        if (v instanceof Number n) return n.intValue();
        try {
            if (v instanceof String s) return Integer.parseInt(s.trim());
        } catch (NumberFormatException ignored) {}
        return 0;
    }

    /** Read total pages from context; returns 1 if missing/invalid. */
    public static int getTotalPages(ComponentContext ctx) {
        Object v = ctx.get(Keys.TOTAL_PAGES);
        if (v instanceof Number n) return n.intValue();
        try {
            if (v instanceof String s) return Integer.parseInt(s.trim());
        } catch (NumberFormatException ignored) {}
        return 1;
    }

    /** Set the page index with clamping to [0.total-1]. */
    public static void setPageIndexClamped(ComponentContext ctx, int newIndex) {
        int total = Math.max(1, getTotalPages(ctx));
        int clamped = Math.max(0, Math.min(total - 1, newIndex));
        ctx.put(Keys.PAGE_INDEX, clamped);
    }

    /** Increment/ decrement of the current page index with clamping. */
    public static void advancePage(ComponentContext ctx, int delta) {
        int idx = getPageIndex(ctx);
        setPageIndexClamped(ctx, idx + delta);
    }

    // ---------------------------------
    // Builder
    // ---------------------------------

    public static class Builder {
        private String chainId;
        private final List<Page> pages = new ArrayList<>();
        private final Map<String, ComponentHandler> handlers = new LinkedHashMap<>();

        public Builder chainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder addPage(Page page) {
            pages.add(page);
            return this;
        }

        public Builder on(String componentId, ComponentHandler handler) {
            handlers.put(componentId, handler);
            return this;
        }

        /**
         * Wires classic next/back/done semantics that advance or finish the chain by
         * modifying ComponentContext using standard keys. You can override any of these
         * by registering your own handler with the same component id via {@link #on(String, ComponentHandler)}.
         */
        public Builder wireNavigation(String backId, String nextId, String doneId) {
            if (backId != null) {
                handlers.putIfAbsent(backId, ctx -> advancePage(ctx, -1));
            }
            if (nextId != null) {
                handlers.putIfAbsent(nextId, ctx -> advancePage(ctx, +1));
            }
            if (doneId != null) {
                handlers.putIfAbsent(doneId, ComponentContext::complete);
            }
            return this;
        }

        public PagedChain build() {
            Objects.requireNonNull(chainId, "chainId (title) is required");
            return new PagedChain(chainId, pages, handlers);
        }
    }
}
