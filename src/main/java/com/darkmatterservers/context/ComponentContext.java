package com.darkmatterservers.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentContext {

    private final String userId;
    private final Map<String, Object> data = new HashMap<>();

    public ComponentContext(String userId) {
        this.userId = userId;
    }

    public String userId() {
        return userId;
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public void putIfAbsent(String key, Object value) {
        data.putIfAbsent(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = data.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public String getString(String key) {
        Object value = data.get(key);
        return value instanceof String str ? str : null;
    }

    public Object remove(String key) {
        return data.remove(key);
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    public void clear() {
        data.clear();
    }

    public Map<String, Object> all() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    public String toString() {
        return "ComponentContext[userId='%s', data=%s]".formatted(userId, data);
    }
}
