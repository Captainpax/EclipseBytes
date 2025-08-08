package com.darkmatterservers.builder;

import com.darkmatterservers.chain.Page;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a Page into:
 * - Embed (title and description; footer shows page counter)
 * - ActionRows (0..2 button rows, optional dropdown row)
 */
public class PageRenderer {

    public record Rendered(MessageEmbed embed, List<ActionRow> rows) {}

    public static Rendered render(String chainTitle, int pageIndex, int totalPages, Page page) {
        // ---- Build embed ----
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("☼ " + chainTitle + " ☼");
        eb.setFooter("Page " + (pageIndex + 1) + " of " + totalPages);
        // Pick a friendly default; tweak/theme later if you want
        eb.setColor(new Color(0x5865F2)); // Discord blurple

        StringBuilder desc = new StringBuilder();
        if (page.line1() != null) desc.append(page.line1()).append("\n");
        if (page.line2() != null) desc.append(page.line2()).append("\n");
        eb.setDescription(desc.toString());

        List<ActionRow> rows = new ArrayList<>();

        // ---- Buttons: two rows of 4 ----
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

        // ---- Dropdown (optional) ----
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

        return new Rendered(eb.build(), rows);
    }
}
