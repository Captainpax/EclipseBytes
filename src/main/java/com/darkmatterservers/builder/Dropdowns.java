package com.darkmatterservers.builder;

import com.darkmatterservers.chain.Page;
import com.darkmatterservers.context.ComponentContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Convenience helpers for creating single dropdowns used by the paged chain system.
 * <p>
 * Notes:
 * - PageRenderer can dynamically override options via ctx key: "<dropdownId>.options" (List<String>).
 * - PageRenderer can also highlight the selected option via ctx key: "<dropdownId>.selected".
 * - Optional auto-advance: set ctx key "<dropdownId>.autoNext" = true to jump to the next page after a pick.
 * - These helpers keep label == value. If you need distinct labels/values later,
 *   extend Page.ComponentRef and the renderer to support pairs.
 */
@SuppressWarnings("unused")
public final class Dropdowns {

    private Dropdowns() {}

    // Discord StringSelectMenu supports up to 25 options
    public static final int MAX_OPTIONS = 25;

    /** Create a dropdown with a placeholder and options (label==value). Extra options beyond 25 are truncated. */
    public static Page.ComponentRef dropdown(String id, String placeholder, Collection<String> options) {
        return Page.ComponentRef.dropdown(id, placeholder, trim(options));
    }

    /** Create a dropdown with a placeholder and options from any collection (label==value via toString). */
    public static Page.ComponentRef dropdownAny(String id, String placeholder, Collection<?> options) {
        return Page.ComponentRef.dropdown(id, placeholder, trimStringify(options));
    }

    /** Create a dropdown with no options yet (useful when you’ll populate later). */
    public static Page.ComponentRef empty(String id, String placeholder) {
        return Page.ComponentRef.dropdown(id, placeholder, null);
    }

    // ---------- Runtime helpers (store state in ComponentContext) ----------

    /** Override the options at runtime (used by PageRenderer). Stores under key "<id>.options". */
    public static void overrideOptions(ComponentContext ctx, String id, Collection<String> options) {
        ctx.put(id + ".options", trim(options));
    }

    /** Same as overrideOptions but stringifies arbitrary values via toString(). */
    public static void overrideOptionsAny(ComponentContext ctx, String id, Collection<?> options) {
        ctx.put(id + ".options", trimStringify(options));
    }

    /** Mark a selected value so the renderer highlights it in the dropdown. Stores under "<id>.selected". */
    public static void markSelected(ComponentContext ctx, String id, String value) {
        ctx.put(id + ".selected", value);
    }

    /** Enable auto-advance: after a selection on this dropdown, advance one page. Stores "<id>.autoNext" = true. */
    public static void enableAutoNext(ComponentContext ctx, String id) {
        ctx.put(id + ".autoNext", Boolean.TRUE);
    }

    /** Disable auto-advance flag for this dropdown. */
    public static void disableAutoNext(ComponentContext ctx, String id) {
        ctx.put(id + ".autoNext", Boolean.FALSE);
    }

    // ---------- internal utils ----------

    private static List<String> trim(Collection<String> src) {
        if (src == null) return null;
        List<String> out = new ArrayList<>(Math.min(src.size(), MAX_OPTIONS));
        int i = 0;
        for (String s : src) {
            if (i++ >= MAX_OPTIONS) break;
            out.add(s);
        }
        return out;
    }

    private static List<String> trimStringify(Collection<?> src) {
        if (src == null) return null;
        List<String> out = new ArrayList<>(Math.min(src.size(), MAX_OPTIONS));
        int i = 0;
        for (Object o : src) {
            if (i++ >= MAX_OPTIONS) break;
            out.add(String.valueOf(o));
        }
        return out;
    }
}
