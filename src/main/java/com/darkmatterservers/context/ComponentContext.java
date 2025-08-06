package com.darkmatterservers.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ComponentContext holds state for a single user's interaction session.
 * Used across chains and component handlers to store and retrieve data.
 */
public class ComponentContext {

    private final String userId;
    private final Map<String, Object> data = new HashMap<>();
    private boolean completed = false;

    public ComponentContext(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the user ID associated with this context.
     */
    public String userId() {
        return userId;
    }

    /**
     * Stores a value by key.
     */
    public void put(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Stores a value only if the key is not already present.
     */
    public void putIfAbsent(String key, Object value) {
        data.putIfAbsent(key, value);
    }

    /**
     * Retrieves a raw object value by key.
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * Retrieves a value by key, casting it to the given type safely.
     */
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = data.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }

    /**
     * Returns the value or a fallback default.
     */
    public Object getOrDefault(String key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    /**
     * Returns the value as a String or null.
     */
    public String getString(String key) {
        Object value = data.get(key);
        return value instanceof String str ? str : null;
    }

    /**
     * Removes the value for the given key.
     */
    public void remove(String key) {
        data.remove(key);
    }

    /**
     * Returns true if the key exists in the context.
     */
    public boolean has(String key) {
        return data.containsKey(key);
    }

    /**
     * Clears all context data.
     */
    public void clear() {
        data.clear();
    }

    /**
     * Returns an unmodifiable view of all context data.
     */
    public Map<String, Object> all() {
        return Collections.unmodifiableMap(data);
    }

    /**
     * Marks this context as complete.
     */
    public void complete() {
        this.completed = true;
    }

    /**
     * Returns whether this context has been marked as complete.
     */
    public boolean isComplete() {
        return completed;
    }

    @Override
    public String toString() {
        return "ComponentContext[userId='%s', completed=%s, data=%s]".formatted(userId, completed, data);
    }
}
