package com.darkmatterservers.builder;

import com.darkmatterservers.chain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Convenience helpers for creating single dropdowns used by the paged chain system.
 *
 * NOTE: The current PageRenderer uses label == value for options.
 * If you need distinct labels/values later, we can extend Page.ComponentRef to carry pairs.
 */
public final class Dropdowns {

    private Dropdowns() {}

    // Discord StringSelectMenu supports up to 25 options
    public static final int MAX_OPTIONS = 25;

    /**
     * Create a dropdown with a placeholder and options (label==value).
     * Extra options beyond 25 are truncated.
     */
    public static Page.ComponentRef dropdown(String id, String placeholder, Collection<String> options) {
        return Page.ComponentRef.dropdown(id, placeholder, trim(options, MAX_OPTIONS));
    }

    /**
     * Create a dropdown with no options yet (useful when you’ll populate later).
     */
    public static Page.ComponentRef empty(String id, String placeholder) {
        return Page.ComponentRef.dropdown(id, placeholder, null);
    }

    /**
     * Utility to safely trim collections.
     */
    private static List<String> trim(Collection<String> src, int max) {
        if (src == null) return null;
        List<String> out = new ArrayList<>(Math.min(src.size(), max));
        int i = 0;
        for (String s : src) {
            if (i++ >= max) break;
            out.add(s);
        }
        return out;
    }
}
