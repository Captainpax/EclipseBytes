package com.darkmatterservers.builder;

import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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

    /**
     * Builds the actual JDA Button component.
     */
    public Button build() {
        return Button.primary(id, label);
    }
}
