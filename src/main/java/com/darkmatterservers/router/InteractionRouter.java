package com.darkmatterservers.router;

import com.darkmatterservers.context.ComponentContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings("unused")
public class InteractionRouter {

    private static final Map<String, ComponentHandler> handlers = new ConcurrentHashMap<>();

    /**
     * Registers a handler for a given component ID.
     * If a handler is already registered, it warns instead of replacing it.
     */
    public static void register(String id, ComponentHandler handler) {
        if (handlers.putIfAbsent(id, handler) == null) {
            System.out.println("✅ Registered handler for ID: " + id);
        } else {
            System.out.println("⚠️ Handler already registered for ID: " + id);
        }
    }

    /**
     * Dispatches a handler by ID and invokes it with the given context.
     */
    public static void handle(String id, ComponentContext ctx) {
        ComponentHandler handler = handlers.get(id);
        if (handler != null) {
            handler.handle(ctx);
        } else {
            System.out.println("❌ No handler found for component: " + id);
        }
    }

    /**
     * Checks if a handler is registered for a given ID.
     */
    public static boolean isRegistered(String id) {
        return handlers.containsKey(id);
    }

    /**
     * Clears all registered component handlers.
     */
    public static void clear() {
        handlers.clear();
        System.out.println("🧹 Cleared all registered component handlers.");
    }

    /**
     * Returns the number of registered handlers.
     */
    public static int count() {
        return handlers.size();
    }
}
