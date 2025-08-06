package com.darkmatterservers.router;

import com.darkmatterservers.context.ComponentContext;

import java.util.HashMap;
import java.util.Map;

public class InteractionRouter {

    private static final Map<String, ComponentHandler> handlers = new HashMap<>();

    public static void register(String id, ComponentHandler handler) {
        if (handlers.containsKey(id)) {
            System.out.println("⚠️ Handler already registered for ID: " + id);
        } else {
            handlers.put(id, handler);
            System.out.println("✅ Registered handler for ID: " + id);
        }
    }

    public static void handle(String id, ComponentContext ctx) {
        ComponentHandler handler = handlers.get(id);
        if (handler != null) {
            handler.handle(ctx);
        } else {
            System.out.println("❌ No handler found for component: " + id);
        }
    }

    public static boolean isRegistered(String id) {
        return handlers.containsKey(id);
    }

    public static void clear() {
        handlers.clear();
        System.out.println("🧹 Cleared all registered component handlers.");
    }

    public static int count() {
        return handlers.size();
    }
}
