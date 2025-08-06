package com.darkmatterservers.chain;

import com.darkmatterservers.router.ComponentHandler;

import java.util.HashMap;
import java.util.Map;

public class NewChain {

    private final Map<String, ChainNode> nodes = new HashMap<>();
    private String entryPoint;

    public NewChain node(String id, String message, Map<String, ComponentHandler> actions) {
        return node(id, message, actions, null);
    }

    public NewChain node(String id, String message, Map<String, ComponentHandler> actions, String defaultAction) {
        nodes.put(id, new ChainNode(id, message, actions, defaultAction));
        return this;
    }

    public NewChain entry(String id) {
        this.entryPoint = id;
        return this;
    }

    public ComponentHandler next(String nextId) {
        return ctx -> ctx.put("nextNode", nextId);
    }

    public ChainNode getNode(String id) {
        return nodes.get(id);
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public static class ChainNode {
        public final String id;
        public final String message;
        public final Map<String, ComponentHandler> actions;
        public final String defaultAction;

        public ChainNode(String id, String message, Map<String, ComponentHandler> actions, String defaultAction) {
            this.id = id;
            this.message = message;
            this.actions = actions;
            this.defaultAction = defaultAction;
        }
    }
}
