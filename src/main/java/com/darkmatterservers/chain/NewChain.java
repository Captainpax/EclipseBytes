package com.darkmatterservers.chain;

import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.ComponentHandler;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * NewChain is a dynamic flow builder that defines a sequence of interaction nodes.
 * Each node has a message, a set of component actions, and a default action.
 */
public class NewChain {

    private final Map<String, ChainNode> nodes = new LinkedHashMap<>();

    /**
     * The starting node of the chain.
     */
    @Getter
    private String entryPoint;

    /**
     * Defines a new node with no default action.
     */
    public NewChain node(String id, String message, Map<String, ComponentHandler> actions) {
        return node(id, message, actions, null);
    }

    /**
     * Defines a new node with a default action key.
     */
    public NewChain node(String id, String message, Map<String, ComponentHandler> actions, String defaultAction) {
        nodes.put(id, new ChainNode(id, message, actions, defaultAction));
        return this;
    }

    /**
     * Sets the starting point for the chain.
     */
    public NewChain entry(String id) {
        this.entryPoint = id;
        return this;
    }

    /**
     * Creates a handler that advances the user to a different node.
     */
    public ComponentHandler next(String nextId) {
        return ctx -> ctx.put("nextNode", nextId);
    }

    /**
     * Creates a handler that marks the context as complete.
     */
    public ComponentHandler complete() {
        return ComponentContext::complete;
    }

    /**
     * Retrieves a chain node by ID.
     */
    public Optional<ChainNode> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    /**
     * Represents a single step in the chain interaction flow.
     */
    public static class ChainNode {
        private final String id;
        private final String message;
        private final Map<String, ComponentHandler> actions;
        private final String defaultAction;

        public ChainNode(String id, String message, Map<String, ComponentHandler> actions, String defaultAction) {
            this.id = id;
            this.message = message;
            this.actions = actions;
            this.defaultAction = defaultAction;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, ComponentHandler> getActions() {
            return actions;
        }

        public String getDefaultAction() {
            return defaultAction;
        }

        public ComponentHandler getHandler(String actionId) {
            return actions.get(actionId);
        }

        public ComponentHandler getDefaultHandler() {
            return defaultAction != null ? actions.get(defaultAction) : null;
        }

        public boolean hasAction(String actionId) {
            return actions.containsKey(actionId);
        }
    }
}
