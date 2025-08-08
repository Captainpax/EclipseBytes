package com.darkmatterservers.builder;

import com.darkmatterservers.chain.Page;

import java.util.List;

/**
 * Convenience helpers for standard nav + custom component creation.
 * Matches the "bytes.next/back/done/buildButton" + single-dropdown pattern
 * used by the paged chain system.
 */
public final class Buttons {

    private Buttons() {}

    // Standard navigation IDs
    public static final String ID_BACK = "bytes.back";
    public static final String ID_NEXT = "bytes.next";
    public static final String ID_DONE = "bytes.done";

    // Prebuilt navigation buttons (labels are what users see)
    public static Page.ComponentRef back()  { return Page.ComponentRef.button(ID_BACK,  "back"); }
    public static Page.ComponentRef next()  { return Page.ComponentRef.button(ID_NEXT,  "next"); }
    public static Page.ComponentRef done()  { return Page.ComponentRef.button(ID_DONE,  "done"); }

    // Generic button factory (id + label)
    public static Page.ComponentRef buildButton(String id, String label) {
        return Page.ComponentRef.button(id, label);
    }

    // Single dropdown factory (id + placeholder + options)
    public static Page.ComponentRef dropdown(String id, String placeholder, List<String> options) {
        return Page.ComponentRef.dropdown(id, placeholder, options);
    }
}
