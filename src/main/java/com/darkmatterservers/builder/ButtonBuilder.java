package com.darkmatterservers.builder;

import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;

public class ButtonBuilder {

    private final String id;
    private final String label;

    public ButtonBuilder(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public ButtonBuilder register(ComponentHandler handler) {
        InteractionRouter.register(id, handler);
        return this;
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }
}
