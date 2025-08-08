package com.darkmatterservers;

import com.darkmatterservers.builder.PageRenderer;
import com.darkmatterservers.chain.Page;
import com.darkmatterservers.chain.PagedChain;
import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.InteractionRouter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * EclipseBytes – a thin runtime helper around the paged chain system.
 */
public class EclipseBytes {

    private final JDA jda;

    /** Simple session store keyed by userId (adjust to messageId/guildId if you prefer). */
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public EclipseBytes(@NotNull AtomicReference<JDA> jdaRef) {
        this.jda = jdaRef.get();
    }

    public void init() {
        System.out.println("✅ EclipseBytes initialized.");
    }

    public void shutdown() {
        sessions.clear();
        InteractionRouter.clear();
        System.out.println("🧹 EclipseBytes shutdown complete.");
    }

    // ---------------------------
    // Public utility methods
    // ---------------------------

    /** Send a plain DM (not part of a chain). */
    public void sendPrivateMessage(String userId, String content) {
        if (validateJdaAndUser(userId)) return;
        jda.retrieveUserById(userId).queue(user ->
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage(content).queue();
                    System.out.println("✅ Sent plain DM to user: " + user.getAsTag());
                })
        );
    }

    /**
     * Start a paged chain in the user's DMs.
     * Creates a fresh ComponentContext for the user and renders page 1.
     */
    public void startDmPagedChain(String userId, PagedChain chain) {
        if (validateJdaAndUser(userId)) return;

        ComponentContext ctx = new ComponentContext(userId);
        ctx.put("pageIndex", 0);
        ctx.put("totalPages", chain.totalPages());

        Session session = new Session(chain, ctx);
        sessions.put(userId, session);

        jda.retrieveUserById(userId).queue(user ->
                user.openPrivateChannel().queue(channel -> renderCurrentPage(session, channel))
        );
    }

    /**
     * Start a paged chain in any channel (e.g., a guild text channel).
     * Session is still keyed by userId; adjust if you want guild+channel scoping.
     */
    public void startChannelPagedChain(String userId, MessageChannel channel, PagedChain chain) {
        ComponentContext ctx = new ComponentContext(userId);
        ctx.put("pageIndex", 0);
        ctx.put("totalPages", chain.totalPages());

        Session session = new Session(chain, ctx);
        sessions.put(userId, session);

        renderCurrentPage(session, channel);
    }

    // ---------------------------
    // JDA interaction handlers
    // ---------------------------

    /** Handle dropdown (StringSelect) interactions. */
    public void handleDropdownInteraction(StringSelectInteractionEvent event) {
        String userId = event.getUser().getId();
        Session session = sessions.get(userId);
        if (session == null) {
            event.deferEdit().queue();
            return; // No active chain
        }

        String componentId = event.getComponentId();
        String selected = event.getValues().isEmpty() ? null : event.getValues().get(0);

        ComponentContext ctx = session.ctx();
        ctx.put("value", selected);              // legacy-friendly
        ctx.put("interactionValue", selected);   // modern-friendly
        ctx.put("rawEvent", event);

        InteractionRouter.handle(componentId, ctx);
        event.deferEdit().queue();

        // Re-render (MessageChannelUnion implements MessageChannel in JDA 5)
        renderPostInteraction(userId, event.getChannel());
    }

    /** Handle button interactions. */
    public void handleButtonInteraction(ButtonInteractionEvent event) {
        String userId = event.getUser().getId();
        Session session = sessions.get(userId);
        if (session == null) {
            event.deferEdit().queue();
            return; // No active chain
        }

        String componentId = event.getComponentId();
        ComponentContext ctx = session.ctx();
        ctx.put("buttonId", componentId);
        ctx.put("rawEvent", event);

        InteractionRouter.handle(componentId, ctx);
        event.deferEdit().queue();

        renderPostInteraction(userId, event.getChannel());
    }

    // ---------------------------
    // Internals
    // ---------------------------

    private void renderPostInteraction(String userId, MessageChannel channel) {
        Session session = sessions.get(userId);
        if (session == null) return;

        if (session.ctx().isComplete()) {
            channel.sendMessage("✅ Setup complete!").queue();
            sessions.remove(userId);
            return;
        }

        renderCurrentPage(session, channel);
    }

    private void renderCurrentPage(Session session, MessageChannel channel) {
        PagedChain chain = session.chain();
        ComponentContext ctx = session.ctx();

        int total = chain.totalPages();
        ctx.put("totalPages", total);

        Object rawIdx = ctx.getOrDefault("pageIndex", 0);
        int idx = (rawIdx instanceof Number) ? ((Number) rawIdx).intValue() : 0;
        idx = chain.clampIndex(idx);
        ctx.put("pageIndex", idx);

        Page page = chain.page(idx);

        PageRenderer.Rendered rendered = PageRenderer.render(chain.chainId(), idx, total, page);
        channel.sendMessageEmbeds(rendered.embed())
                .setComponents(rendered.rows().toArray(ActionRow[]::new))
                .queue();
    }

    private boolean validateJdaAndUser(String userId) {
        if (jda == null) {
            System.err.println("❌ Cannot send message — JDA is null");
            return true;
        }
        if (userId == null || userId.isBlank()) {
            System.err.println("❌ Cannot send message — userId is null/blank");
            return true;
        }
        return false;
    }

    // ---------------------------
    // Session record
    // ---------------------------

    private record Session(PagedChain chain, ComponentContext ctx) {
        public Session {
            Objects.requireNonNull(chain, "chain");
            Objects.requireNonNull(ctx, "ctx");
        }
    }

    // ---------------------------
    // Legacy helper (kept because other code calls it)
    // ---------------------------

    public String formatMessage(String header, String body) {
        return "**" + header + "**\n> " + body;
    }
}
