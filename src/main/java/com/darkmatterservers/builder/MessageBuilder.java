package com.darkmatterservers.builder;

import com.darkmatterservers.router.InteractionRouter;
import com.darkmatterservers.router.ComponentHandler;

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

    // --- Inner component records ---

    public record ButtonData(String id, String label) {
    }

    public record DropdownData(String id, List<String> options) {
    }
}
