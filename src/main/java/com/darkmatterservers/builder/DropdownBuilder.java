package com.darkmatterservers.builder;

import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropdownBuilder {

    private final String id;
    private final List<Option> options = new ArrayList<>();

    public DropdownBuilder(String id) {
        this.id = id;
    }

    public DropdownBuilder withOption(String value) {
        return withOption(value, value); // label = value
    }

    public DropdownBuilder withOption(String value, String label) {
        options.add(new Option(value, label));
        return this;
    }

    public DropdownBuilder register(ComponentHandler handler) {
        InteractionRouter.register(id, handler);
        return this;
    }

    public String id() {
        return id;
    }

    public List<Option> options() {
        return Collections.unmodifiableList(options);
    }

    public record Option(String value, String label) {
    }
}
