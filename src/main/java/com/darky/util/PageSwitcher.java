package com.darky.util;

import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;

import static com.darky.core.Messages.editMessage;
import static com.darky.core.Messages.sendMessage;

public class PageSwitcher {

    public static void newPageSwitcher(CommandEvent event, TextChannel channel, ArrayList<MessageEmbed.Field> fields, String title, String description, int itemsperpage) {
        sendMessage(event.getCache(), event.getChannel(), "Loading please wait!","Loading...", event.getAuthor()).queue(
                msg -> {
                    onPageSwitch(msg, 0, itemsperpage, fields, title, description, event);
                }
        );
    }

    private static void onPageSwitch(Message msg, final int position, int itemsperpage, ArrayList<MessageEmbed.Field> fields, String title, String description, CommandEvent cmdevent) {
        EmbedBuilder builder = new EmbedBuilder();
        int i = 0;
        while (itemsperpage>i) {
            builder.addField(fields.get(position+i));
            i++;
        }
        final int positionnow = position+itemsperpage;
        if (position!=0)
            msg.addReaction("⬅").queue();
        msg.addReaction("❌").queue();
        if (position!=fields.size()-1)
            msg.addReaction("➡").queue();
        editMessage(msg, cmdevent.getCache(), title, description, cmdevent.getAuthor(), false, null, builder).queue();
        msg.getJDA().asBot().getShardManager().addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
                if (event.getUser().getIdLong()!=event.getJDA().getSelfUser().getIdLong()) {
                    if (event.getMessageIdLong()==msg.getIdLong()) {
                        switch (event.getReactionEmote().getName()) {
                            case "⬅":
                                msg.clearReactions().queue(
                                        succes -> onPageSwitch(msg, positionnow-(itemsperpage*2), itemsperpage, fields, title, description, cmdevent)
                                );
                                break;
                            case "❌":
                                msg.delete().queue();
                                break;
                            case "➡":
                                msg.clearReactions().queue(
                                    succes -> onPageSwitch(msg, positionnow, itemsperpage, fields, title, description, cmdevent)
                                );
                                break;
                        }
                        msg.getJDA().asBot().getShardManager().removeEventListener(this);
                    }
                }
            }
        });
    }

}
