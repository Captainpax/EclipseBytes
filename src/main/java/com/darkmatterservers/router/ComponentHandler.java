package com.darkmatterservers.router;

import com.darkmatterservers.context.ComponentContext;
@SuppressWarnings("unused")
@FunctionalInterface
public interface ComponentHandler {
    void handle(ComponentContext ctx);
}
