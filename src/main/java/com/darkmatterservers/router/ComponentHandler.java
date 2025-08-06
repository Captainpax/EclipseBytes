package com.darkmatterservers.router;

import com.darkmatterservers.context.ComponentContext;

@FunctionalInterface
public interface ComponentHandler {
    void handle(ComponentContext ctx);
}
