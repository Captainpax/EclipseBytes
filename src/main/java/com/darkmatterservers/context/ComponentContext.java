package com.darkmatterservers.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ComponentContext holds state for a single user's interaction session.
 * Used across chains and component handlers to store and retrieve data.
 *
 * Conventions used by EclipseBytes:
 *  - For dropdowns, Bytes sets:
 *      * "value"              -> String (selected value or null)
 *      * "interactionValue"   -> String (same as value; convenience)
 *      * "rawEvent"           -> the JDA event object
 *  - For buttons, Bytes sets:
 *      * "buttonId"           -> String (component id)
 *      * "rawEvent"           -> the JDA event object
 *
 * You can store anything else you want alongside those keys.
 */
public class ComponentContext {

    private final String userId;
    private final Map<String, Object> data = new HashMap<>();
    private boolean completed = false;

    public ComponentContext(String userId) {
        this.userId = userId;
    }

    // -------------------- Identity --------------------

    /** Returns the user ID associated with this context. */
    public String userId() {
        return userId;
    }

    // -------------------- Mutators --------------------

    /** Stores a value by key. */
    public void put(String key, Object value) {
        data.put(key, value);
    }

    /** Stores a value only if the key is not already present. */
    public void putIfAbsent(String key, Object value) {
        data.putIfAbsent(key, value);
    }

    /**
     * Stores a value only if absent and returns the existing/new value.
     * Handy when you want to assign a default and immediately use it.
     */
    @SuppressWarnings("unchecked")
    public <T> T putIfAbsentReturn(String key, T value) {
        Object existing = data.putIfAbsent(key, value);
        return (T) (existing != null ? existing : value);
    }

    /** Removes the value for the given key. */
    public void remove(String key) {
        data.remove(key);
    }

    /** Clears all context data. */
    public void clear() {
        data.clear();
    }

    // -------------------- Accessors --------------------

    /** Retrieves a raw object value by key. */
    public Object get(String key) {
        return data.get(key);
    }

    /** Retrieves a value by key, casting it to the given type safely. */
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = data.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }

    /** Returns the value or a fallback default. */
    public Object getOrDefault(String key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    /** Returns true if the key exists in the context. */
    public boolean has(String key) {
        return data.containsKey(key);
    }

    /** Returns an unmodifiable view of all context data. */
    public Map<String, Object> all() {
        return Collections.unmodifiableMap(data);
    }

    // -------------------- Typed convenience getters --------------------

    /** Returns the value as a String or null. */
    public String getString(String key) {
        Object value = data.get(key);
        return value instanceof String str ? str : null;
        // (If needed, expand to String.valueOf for non-strings, but null-safety is nicer here.)
    }

    /** Returns the value as an Integer or null (parses String values when possible). */
    public Integer getInt(String key) {
        Object value = data.get(key);
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) {
            try { return Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    /** Returns the value as a Boolean or null (parses String values \"true\"/\"false\"). */
    public Boolean getBoolean(String key) {
        Object value = data.get(key);
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return Boolean.parseBoolean(s.trim());
        return null;
    }

    // -------------------- Interaction helpers --------------------

    /**
     * Returns the current selection/value from the interaction.
     * Bytes sets both \"value\" and \"interactionValue\" for convenience; we check both.
     */
    public String interactionValue() {
        String v = getString("interactionValue");
        if (v != null) return v;
        return getString("value");
    }

    /**
     * Returns multiple selected values if your UI supports multi-select.
     * Convention: store under \"interactionValues\" or \"values\" as List<String>.
     */
    @SuppressWarnings("unchecked")
    public List<String> interactionValues() {
        Object vs = data.get("interactionValues");
        if (vs instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String) {
            return (List<String>) list;
        }
        vs = data.get("values");
        if (vs instanceof List<?> list2 && !list2.isEmpty() && list2.get(0) instanceof String) {
            return (List<String>) list2;
        }
        String single = interactionValue();
        if (single != null) {
            List<String> one = new ArrayList<>(1);
            one.add(single);
            return one;
        }
        return List.of();
    }

    // -------------------- Completion --------------------

    /** Marks this context as complete. */
    public void complete() {
        this.completed = true;
    }

    /** Returns whether this context has been marked as complete. */
    public boolean isComplete() {
        return completed;
    }

    @Override
    public String toString() {
        return "ComponentContext[userId='%s', completed=%s, data=%s]".formatted(userId, completed, data);
    }
}
