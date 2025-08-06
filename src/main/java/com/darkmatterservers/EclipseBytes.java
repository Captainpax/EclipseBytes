package com.darkmatterservers;

import com.darkmatterservers.builder.DropdownBuilder;
import com.darkmatterservers.builder.MessageBuilder;
import com.darkmatterservers.context.ComponentContext;
import com.darkmatterservers.router.ComponentHandler;
import com.darkmatterservers.router.InteractionRouter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EclipseBytes {

    private final JDA jda;

    public EclipseBytes(@NotNull AtomicReference<JDA> jdaRef) {
        this.jda = jdaRef.get();
    }

    public void init() {
        System.out.println("✅ EclipseBytes initialized.");
    }

    public void shutdown() {
        InteractionRouter.clear();
        System.out.println("🧹 EclipseBytes shutdown complete.");
    }

    public void sendPrivateMessage(String userId, String content) {
        if (jda == null || userId == null || userId.isBlank()) {
            System.err.println("❌ Cannot send DM — JDA or userId is null");
            return;
        }

        jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessage(content).queue();
                System.out.println("✅ Sent plain DM to user: " + user.getAsTag());
            });
        });
    }

    public void sendPrivateDropdown(
            String userId,
            String message,
            String dropdownId,
            List<String> options,
            ComponentHandler handler
    ) {
        if (jda == null || userId == null || userId.isBlank()) {
            System.err.println("❌ Cannot send dropdown — JDA or userId is null");
            return;
        }

        // Build dropdown
        DropdownBuilder builder = new DropdownBuilder(dropdownId);
        options.forEach(builder::withOption);
        builder.register(handler);

        StringSelectMenu menu = StringSelectMenu.create(dropdownId)
                .addOptions(builder.options().stream()
                        .map(opt -> SelectOption.of(opt.label(), opt.value()))
                        .toList())
                .build();

        // Send a message
        jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                MessageBuilder msg = new MessageBuilder()
                        .withContent(message);
                channel.sendMessage(msg.getContent())
                        .addActionRow(menu)
                        .queue();
                System.out.println("✅ Sent DM with dropdown to user: " + user.getAsTag());
            });
        });
    }

    public void handleDropdownInteraction(StringSelectInteractionEvent event) {
        String id = event.getComponentId();
        String userId = event.getUser().getId();

        ComponentContext ctx = new ComponentContext(userId);
        ctx.put("value", event.getValues().isEmpty() ? null : event.getValues().get(0));
        ctx.put("rawEvent", event);

        InteractionRouter.handle(id, ctx);
        event.deferEdit().queue();
    }

    public String formatMessage(String header, String body) {
        return "**" + header + "**\n> " + body;
    }
}
