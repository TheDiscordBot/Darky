package com.darky.listeners;

import com.darky.core.Database;
import com.darky.core.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MentionListener extends ListenerAdapter {

    private Database database;

    public MentionListener(Database database) {
        this.database = database;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(event.getGuild().getSelfMember().getAsMention())) {
            // TODO: Replace 123 test with version
            EmbedBuilder builder = new EmbedBuilder().setTitle("Hi!").setDescription("I'm better than bulby :P")
                    .addField("Prefix", "d!", true)
                    .addField("JDA Version", event.getClass().getPackage().getImplementationVersion(), true)
                    .addField("JDBC Connector Version", "123 test", true);
            event.getChannel().sendMessage(Messages.build(database, event.getChannel(), null, null, event.getAuthor(), false, null, builder)).queue();

        }
    }
}
