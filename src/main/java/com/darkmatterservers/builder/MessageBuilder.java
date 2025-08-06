package com.darkmatterservers.builder;

import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    private String content;
    private final List<ButtonData> buttons = new ArrayList<>();
    private final List<DropdownData> dropdowns = new ArrayList<>();

    public MessageBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public MessageBuilder withButton(String id, String label, ComponentHandler handler) {
        ButtonData button = new ButtonData(id, label);
        buttons.add(button);
        InteractionRouter.register(id, handler);
        return this;
    }

    public MessageBuilder withDropdown(String id, List<String> options, ComponentHandler handler) {
        DropdownData dropdown = new DropdownData(id, options);
        dropdowns.add(dropdown);
        InteractionRouter.register(id, handler);
        return this;
    }

    public String getContent() {
        return content;
    }

    public List<ButtonData> getButtons() {
        return buttons;
    }

    public List<DropdownData> getDropdowns() {
        return dropdowns;
    }

    /**
     * Builds a list of ActionRows from attached buttons and dropdowns.
     */
    public List<ActionRow> getActionRows() {
        List<ActionRow> rows = new ArrayList<>();

        if (!buttons.isEmpty()) {
            List<Button> buttonComponents = new ArrayList<>();
            for (ButtonData btn : buttons) {
                buttonComponents.add(Button.primary(btn.id(), btn.label()));
            }
            rows.add(ActionRow.of(buttonComponents));
        }

        for (DropdownData dd : dropdowns) {
            StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(dd.id());
            for (String option : dd.options()) {
                menuBuilder.addOptions(SelectOption.of(option, option));
            }
            rows.add(ActionRow.of(menuBuilder.build()));
        }

        return rows;
    }

    // --- Inner component records ---

    public record ButtonData(String id, String label) {
    }

    public record DropdownData(String id, List<String> options) {
    }
}
