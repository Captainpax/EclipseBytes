package com.darkmatterservers.chain;

import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.ComponentHandler;

public class TestChainRunner {

    private final NewChain chain;
    private final ComponentContext ctx;

    public TestChainRunner(NewChain chain, String userId) {
        this.chain = chain;
        this.ctx = new ComponentContext(userId);
    }

    public void simulate() {
        String currentNodeId = chain.getEntryPoint();

        while (currentNodeId != null) {
            NewChain.ChainNode node = chain.getNode(currentNodeId);

            if (node == null) {
                System.out.println("❌ Node not found: " + currentNodeId);
                break;
            }

            System.out.println("\n📨 [Chain: " + node.id + "]");
            System.out.println(node.message);

            if (node.actions.isEmpty()) {
                System.out.println("⚠️ No actions available.");
                break;
            }

            System.out.println("   • Options: " + node.actions.keySet());

            String chosenAction = node.defaultAction;
            if (chosenAction == null || !node.actions.containsKey(chosenAction)) {
                System.out.println("⚠️ No valid default action for this node. Stopping simulation.");
                break;
            }

            System.out.println("   → Simulating action: '" + chosenAction + "'");
            ComponentHandler handler = node.actions.get(chosenAction);
            handler.handle(ctx);

            // If no nextNode is set, assume we're done and exit
            if (ctx.has("nextNode")) {
                currentNodeId = (String) ctx.get("nextNode");
                ctx.remove("nextNode");
            } else {
                break;
            }
        }
    }
}
