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
 * - The PageRenderer will dynamically override options if ctx has "<dropdownId>.options" (List<String>).
 * - These helpers keep label == value. If you need distinct labels/values later,
 *   we can extend Page.ComponentRef and renderer to support pairs.
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

    /**
     * Override the options at runtime (used by PageRenderer).
     * Stores under key "<id>.options" inside the provided context.
     */
    public static void overrideOptions(ComponentContext ctx, String id, Collection<String> options) {
        ctx.put(id + ".options", trim(options));
    }

    /** Same as overrideOptions but stringifies arbitrary values via toString(). */
    public static void overrideOptionsAny(ComponentContext ctx, String id, Collection<?> options) {
        ctx.put(id + ".options", trimStringify(options));
    }

    // ---------- internal utils ----------

    private static List<String> trim(Collection<String> src) {
        if (src == null) return null;
        List<String> out = new ArrayList<>(Math.min(src.size(), Dropdowns.MAX_OPTIONS));
        int i = 0;
        for (String s : src) {
            if (i++ >= Dropdowns.MAX_OPTIONS) break;
            out.add(s);
        }
        return out;
    }

    private static List<String> trimStringify(Collection<?> src) {
        if (src == null) return null;
        List<String> out = new ArrayList<>(Math.min(src.size(), Dropdowns.MAX_OPTIONS));
        int i = 0;
        for (Object o : src) {
            if (i++ >= Dropdowns.MAX_OPTIONS) break;
            out.add(String.valueOf(o));
        }
        return out;
    }
}
