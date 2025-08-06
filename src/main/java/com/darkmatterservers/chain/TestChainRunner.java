package com.darkmatterservers.chain;

import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.ComponentHandler;

import java.util.Optional;

/**
 * Simulates the execution of a NewChain from start to finish using default actions.
 * Useful for testing chain logic in isolation.
 */
public class TestChainRunner {

    private final NewChain chain;
    private final ComponentContext ctx;

    public TestChainRunner(NewChain chain, String userId) {
        this.chain = chain;
        this.ctx = new ComponentContext(userId);
    }

    /**
     * Starts the simulation loop from the entry point.
     */
    public void simulate() {
        String currentNodeId = chain.getEntryPoint();

        if (currentNodeId == null) {
            System.out.println("❌ No entry point defined for this chain.");
            return;
        }

        while (currentNodeId != null) {
            Optional<NewChain.ChainNode> optionalNode = chain.getNode(currentNodeId);

            if (optionalNode.isEmpty()) {
                System.out.println("❌ Node not found: " + currentNodeId);
                break;
            }

            NewChain.ChainNode node = optionalNode.get();
            System.out.println("\n📨 [Chain Node: " + node.getId() + "]");
            System.out.println("📋 Message: " + node.getMessage());

            if (node.getActions().isEmpty()) {
                System.out.println("⚠️ No actions available at this node.");
                break;
            }

            System.out.println("🔘 Options: " + node.getActions().keySet());

            String defaultAction = node.getDefaultAction();
            if (defaultAction == null || !node.hasAction(defaultAction)) {
                System.out.println("⚠️ No valid default action. Simulation ending.");
                break;
            }

            System.out.println("   → Executing default action: '" + defaultAction + "'");
            ComponentHandler handler = node.getHandler(defaultAction);
            handler.handle(ctx);

            if (ctx.has("nextNode")) {
                currentNodeId = ctx.getString("nextNode");
                ctx.remove("nextNode");
            } else {
                System.out.println("🏁 Chain ended. No further steps.");
                break;
            }
        }

        System.out.println("\n✅ Chain simulation complete.");
    }
}
