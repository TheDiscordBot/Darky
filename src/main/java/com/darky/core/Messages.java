package com.darky.core;

import com.darky.core.caching.Cache;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import java.awt.*;
import java.util.Date;

public class Messages {

    public static MessageEmbed buildEmbed(String title, String description, User author, boolean timestamp, Color color, EmbedBuilder embedBuilder, Cache cache) {
        if (embedBuilder==null) embedBuilder = new EmbedBuilder();
        if (author!=null) embedBuilder.setAuthor(author.getName(), author.getAvatarUrl(), author.getAvatarUrl());
        if (timestamp) embedBuilder.setTimestamp(new Date().toInstant());
        if (title!=null) embedBuilder.setTitle(title);
        if (description!=null) embedBuilder.setDescription(description);
        if (color!=null) embedBuilder.setColor(color); else embedBuilder.setColor(cache.getUser(author).getEmbedcolor());
        return embedBuilder.build();
    }

    public static MessageAction sendMessage(Cache cache, TextChannel channel, String title) {
        return channel.sendMessage(buildEmbed(title, null, null, false, null, new EmbedBuilder(), cache));
    }

    public static MessageAction sendMessage(Cache cache, TextChannel channel, String title, String description) {
        return channel.sendMessage(buildEmbed(title, description, null, false, null, new EmbedBuilder(), cache));
    }

    public static MessageAction sendMessage(Cache cache, TextChannel channel, String title, String description, User author) {
        return channel.sendMessage(buildEmbed(title, description, author, false, null, new EmbedBuilder(), cache));
    }

    public static MessageAction sendMessage(Cache cache, TextChannel channel, String title, String description, User author, boolean timestamp) {
        return channel.sendMessage(buildEmbed(title, description, author, timestamp, null, new EmbedBuilder(), cache));
    }

    public static MessageAction sendMessage(Cache cache, TextChannel channel, String title, String description, User author, boolean timestamp, Color color) {
        return channel.sendMessage(buildEmbed(title, description, author, timestamp, color, new EmbedBuilder(), cache));
    }

    public static MessageAction sendMessage(Cache cache, TextChannel channel, String title, String description, User author, boolean timestamp, Color color, EmbedBuilder embedBuilder) {
        return channel.sendMessage(buildEmbed(title, description, author, timestamp, color, embedBuilder, cache));
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------

    public static MessageAction editMessage(Message msg, Cache cache, String title) {
        return msg.editMessage(buildEmbed(title, null, null, false, null, new EmbedBuilder(), cache));
    }

    public static MessageAction editMessage(Message msg, Cache cache, String title, String description) {
        return msg.editMessage(buildEmbed(title, description, null, false, null, new EmbedBuilder(), cache));
    }

    public static MessageAction editMessage(Message msg, Cache cache, String title, String description, User author) {
        return msg.editMessage(buildEmbed(title, description, author, false, null, new EmbedBuilder(), cache));
    }

    public static MessageAction editMessage(Message msg, Cache cache, String title, String description, User author, boolean timestamp) {
        return msg.editMessage(buildEmbed(title, description, author, timestamp, null, new EmbedBuilder(), cache));
    }

    public static MessageAction editMessage(Message msg, Cache cache, String title, String description, User author, boolean timestamp, Color color) {
        return msg.editMessage(buildEmbed(title, description, author, timestamp, color, new EmbedBuilder(), cache));
    }

    public static MessageAction editMessage(Message msg, Cache cache, String title, String description, User author, boolean timestamp, Color color, EmbedBuilder embedBuilder) {
        return msg.editMessage(buildEmbed(title, description, author, timestamp, color, embedBuilder, cache));
    }
}
