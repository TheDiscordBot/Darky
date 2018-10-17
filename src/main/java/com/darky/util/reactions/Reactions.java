package com.darky.util.reactions;

import com.darky.util.emotes.Emotes;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.darky.core.Messages.sendMessage;

public class Reactions extends ListenerAdapter {

    public static final String YES_EMOTE = "✅";
    public static final String NO_EMOTE = "❌";
    public static final String ROBOT = "\uD83E\uDD16";

    public static void newMenu(CommandEvent commandEvent, User user, TextChannel channel, String title, String description, Builder... builders) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\n");
        for (Builder builder : builders) {
            stringBuilder.append(builder.getTextEmote());
            stringBuilder.append(" - ");
            stringBuilder.append(builder.getText());
            stringBuilder.append("\n");
        }
        embedBuilder.appendDescription(stringBuilder.toString());
        newMenu(commandEvent, user, channel, embedBuilder, builders);
    }

    public static void newMenu(CommandEvent commandEvent, User user, TextChannel channel, EmbedBuilder embedbuilder, Builder... builders) {
        sendMessage(commandEvent.getDatabase(), channel, null, null, user, true, null, embedbuilder).queue(
                msg -> {
                    HashMap<String, Consumer<Menu>> hashMap = new HashMap<>();
                    Arrays.asList(builders).forEach(
                            oneBuilder -> msg.addReaction(oneBuilder.getEmote()).queue(v -> hashMap.put(oneBuilder.getEmote(), oneBuilder.getAction()))
                    );

                    ListenerAdapter listener = new ListenerAdapter() {
                        public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
                            if (event.getMember().getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong())
                                return;
                            if (msg.getIdLong() != event.getMessageIdLong()) return;
                            if (user.getIdLong() != event.getMember().getUser().getIdLong()) return;

                            String emote;
                            if (event.getReactionEmote().getEmote() == null)
                                emote = event.getReactionEmote().getName();
                            else
                                emote = Emotes.getFromMention(event.getReactionEmote().getEmote().getAsMention()).getAsReaction();

                            if (hashMap.containsKey(emote)) {
                                hashMap.get(emote).accept(new Menu(user.getIdLong(), msg.getIdLong(), hashMap));
                                msg.clearReactions().queue();
                                commandEvent.getJDA().asBot().getShardManager().removeEventListener(this);
                            }
                        }
                    };
                    commandEvent.getJDA().asBot().getShardManager().addEventListener(listener);
                }
        );
    }

}
