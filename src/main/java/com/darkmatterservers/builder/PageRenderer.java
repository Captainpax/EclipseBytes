package com.darkmatterservers.builder;

import com.darkmatterservers.chain.Page;
import com.darkmatterservers.context.ComponentContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a Page into:
 * - Embed (title and description; footer shows page counter)
 * - ActionRows (0..2 button rows, optional dropdown row)
 * <p>
 * Dynamic behavior:
 * - If the context contains a key "<dropdownId>.options" with a List<String>, those
 *   options override the dropdown's baked-in list for this render.
 * - Button styles are honored from Page.ComponentRef.style(); defaults to PRIMARY when null.
 */
@SuppressWarnings("unused")
public class PageRenderer {

    public record Rendered(MessageEmbed embed, List<ActionRow> rows) {}

    public static Rendered render(String chainTitle, int pageIndex, int totalPages, Page page) {
        return render(chainTitle, pageIndex, totalPages, page, null);
    }

    public static Rendered render(String chainTitle, int pageIndex, int totalPages, Page page, ComponentContext ctx) {
        // ---- Build embed ----
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("☼ " + chainTitle + " ☼");
        eb.setFooter("Page " + (pageIndex + 1) + " of " + totalPages);
        eb.setColor(new Color(0x5865F2)); // default theme color (Discord blurple)

        StringBuilder desc = new StringBuilder();
        if (page.line1() != null && !page.line1().isBlank()) desc.append(page.line1()).append('\n');
        if (page.line2() != null && !page.line2().isBlank()) desc.append(page.line2()).append('\n');
        eb.setDescription(desc.toString());

        List<ActionRow> rows = new ArrayList<>();

        // ---- Buttons: two rows of 4 ----
        Page.ComponentRef[] btns = page.buttons();
        List<Button> row1 = new ArrayList<>(4);
        List<Button> row2 = new ArrayList<>(4);
        for (int i = 0; i < btns.length; i++) {
            Page.ComponentRef ref = btns[i];
            if (ref == null || !ref.isButton()) continue;

            ButtonStyle style = ButtonStyle.PRIMARY;
            try {
                if (ref.style() != null) style = ref.style();
            } catch (Throwable ignored) { /* backward compat if style() missing */ }

            Button b = switch (style) {
                case SECONDARY -> Button.secondary(ref.id(), ref.label());
                case SUCCESS   -> Button.success(ref.id(), ref.label());
                case DANGER    -> Button.danger(ref.id(), ref.label());
                case LINK      -> Button.link(ref.id(), ref.label());
                default        -> Button.primary(ref.id(), ref.label());
            };

            if (i < 4) row1.add(b); else row2.add(b);
        }
        if (!row1.isEmpty()) rows.add(ActionRow.of(row1));
        if (!row2.isEmpty()) rows.add(ActionRow.of(row2));

        // ---- Dropdown (optional) ----
        if (page.dropdown() != null && page.dropdown().isDropdown()) {
            var dd = page.dropdown();
            String placeholder = (dd.label() == null || dd.label().isBlank()) ? "Select an option" : dd.label();
            StringSelectMenu.Builder menu = StringSelectMenu.create(dd.id()).setPlaceholder(placeholder);

            List<String> opts = dd.options();
            // Dynamic override: ctx key "<id>.options" -> List<String>
            if (ctx != null) {
                Object override = ctx.get(dd.id() + ".options");
                if (override instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof String)) {
                    @SuppressWarnings("unchecked")
                    List<String> cast = (List<String>) list;
                    opts = cast;
                }
            }

            if (opts != null) {
                for (String opt : limitOptions(opts)) { // Discord limit for StringSelect is 25
                    if (opt == null) continue;
                    menu.addOptions(SelectOption.of(opt, opt));
                }
            }
            rows.add(ActionRow.of(menu.build()));
        }

        return new Rendered(eb.build(), rows);
    }

    private static List<String> limitOptions(List<String> src) {
        if (src == null) return null;
        int end = Math.min(src.size(), Math.max(0, 25));
        return new ArrayList<>(src.subList(0, end));
    }
}
