package com.darkmatterservers.builder;

import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropdownBuilder {

    private final String id;
    private final List<Option> options = new ArrayList<>();

    public DropdownBuilder(String id) {
        this.id = id;
    }

    public void withOption(String value) {
        withOption(value, value);
    }

    public void withOption(String value, String label) {
        options.add(new Option(value, label));
    }

    public void register(ComponentHandler handler) {
        InteractionRouter.register(id, handler);
    }

    public String id() {
        return id;
    }

    public List<Option> options() {
        return Collections.unmodifiableList(options);
    }

    /**
     * Builds a JDA StringSelectMenu component.
     */
    public StringSelectMenu build() {
        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(id);
        for (Option opt : options) {
            menuBuilder.addOptions(SelectOption.of(opt.label(), opt.value()));
        }
        return menuBuilder.build();
    }

    public record Option(String value, String label) {
    }
}
