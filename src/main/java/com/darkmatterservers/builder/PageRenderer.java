package com.darkmatterservers.builder;

import com.darkmatterservers.chain.Page;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a Page into:
 * - String content (title + page counter + two lines)
 * - ActionRows (0..2 button rows, optional dropdown row)
 */
public class PageRenderer {

    public record Rendered(String content, List<ActionRow> rows) {}

    public static Rendered render(String chainTitle, int pageIndex, int totalPages, Page page) {
        // Content block (uniform)
        StringBuilder sb = new StringBuilder();
        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        sb.append("***☼*** ").append(chainTitle).append(" ***☼***\n");
        sb.append("\t ☼ Page `").append(pageIndex + 1).append("` of `").append(totalPages).append("` ☼\n\n");
        if (page.line1() != null) sb.append(page.line1()).append("\n");
        if (page.line2() != null) sb.append(page.line2()).append("\n");
        sb.append("\n");

        List<ActionRow> rows = new ArrayList<>();

        // Buttons: two rows of 4
        Page.ComponentRef[] btns = page.buttons();
        List<Button> row1 = new ArrayList<>(4);
        List<Button> row2 = new ArrayList<>(4);
        for (int i = 0; i < btns.length; i++) {
            Page.ComponentRef ref = btns[i];
            if (ref == null || !ref.isButton()) continue;
            Button b = Button.primary(ref.id(), ref.label());
            if (i < 4) row1.add(b);
            else row2.add(b);
        }
        if (!row1.isEmpty()) rows.add(ActionRow.of(row1));
        if (!row2.isEmpty()) rows.add(ActionRow.of(row2));

        // Dropdown (optional)
        if (page.dropdown() != null && page.dropdown().isDropdown()) {
            var dd = page.dropdown();
            StringSelectMenu.Builder menu = StringSelectMenu.create(dd.id()).setPlaceholder(dd.label());
            if (dd.options() != null) {
                for (String opt : dd.options()) {
                    menu.addOptions(SelectOption.of(opt, opt));
                }
            }
            rows.add(ActionRow.of(menu.build()));
        }

        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        return new Rendered(sb.toString(), rows);
    }
}
