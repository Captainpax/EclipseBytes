package com.darkmatterservers.chain;

import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;

import java.util.*;
import java.util.function.Consumer;

/**
 * A paged chain with uniform pages.
 * Use Builder to declare:
 * - chainId (title)
 * - pages (Page objects)
 * - handlers for buttons/dropdowns (by component id)
 *
 * Runtime state:
 * - stores "pageIndex" in the ComponentContext
 */
public class PagedChain {

    private final String chainId;
    private final List<Page> pages;
    private final Map<String, ComponentHandler> handlers;

    private PagedChain(String chainId, List<Page> pages, Map<String, ComponentHandler> handlers) {
        if (pages.isEmpty()) throw new IllegalArgumentException("PagedChain requires at least one page");
        this.chainId = chainId;
        this.pages = List.copyOf(pages);
        this.handlers = Map.copyOf(handlers);
        // register all handlers
        this.handlers.forEach(InteractionRouter::register);
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

    // --- Builder ---

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
         * Utility wiring for classic next/back/done semantics that advance
         * or finish the chain by modifying the ComponentContext.
         * You can also override these with your own handler when calling .on(id, handler).
         */
        public Builder wireNavigation(String backId, String nextId, String doneId) {
            if (backId != null) {
                handlers.putIfAbsent(backId, ctx -> {
                    int idx = Optional.ofNullable((Integer) ctx.get("pageIndex")).orElse(0);
                    ctx.put("pageIndex", Math.max(0, idx - 1));
                });
            }
            if (nextId != null) {
                handlers.putIfAbsent(nextId, ctx -> {
                    int idx = Optional.ofNullable((Integer) ctx.get("pageIndex")).orElse(0);
                    int max = Optional.ofNullable((Integer) ctx.get("totalPages")).orElse(1) - 1;
                    ctx.put("pageIndex", Math.min(max, idx + 1));
                });
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
