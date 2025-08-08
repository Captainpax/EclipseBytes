package com.darkmatterservers.chain;

import java.util.List;
import java.util.Objects;

/**
 * A single uniform page in a paged chain.
 * - Two text lines
 * - Up to 8 buttons (indexes 0..7). Two rows of 4 are rendered if non-null.
 * - Optional one dropdown.
 */
public class Page {

    private final String line1;
    private final String line2;

    // 8 slots (0..7). Null entries are not rendered.
    private final ComponentRef[] buttons = new ComponentRef[8];

    // Optional single dropdown
    private ComponentRef dropdown;

    public Page(String line1, String line2) {
        this.line1 = line1;
        this.line2 = line2;
    }

    public Page withButton(int index, ComponentRef button) {
        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("Button index must be 0..7");
        }
        buttons[index] = button;
        return this;
    }

    public Page withDropdown(ComponentRef dropdown) {
        this.dropdown = dropdown;
        return this;
    }

    public String line1() {
        return line1;
    }

    public String line2() {
        return line2;
    }

    public ComponentRef[] buttons() {
        return buttons;
    }

    public ComponentRef dropdown() {
        return dropdown;
    }

    /**
     * Lightweight reference describing a component to render+wire.
     * type: "button" or "dropdown"
     * id: unique id used by the router
     * label: button label OR dropdown placeholder
     * options: for dropdown only (nullable for buttons)
     */
    public record ComponentRef(String type, String id, String label, List<String> options) {
        public static ComponentRef button(String id, String label) {
            return new ComponentRef("button", id, label, null);
        }

        public static ComponentRef dropdown(String id, String placeholder, List<String> options) {
            return new ComponentRef("dropdown", id, placeholder, options);
        }

        public boolean isButton() { return Objects.equals(type, "button"); }
        public boolean isDropdown() { return Objects.equals(type, "dropdown"); }
    }
}
