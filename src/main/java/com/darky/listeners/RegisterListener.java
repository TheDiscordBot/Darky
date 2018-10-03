package com.darky.listeners;

import com.darky.core.Database;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class RegisterListener extends ListenerAdapter {

    private Database database;

    public RegisterListener(Database database) {
        this.database = database;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        database.createIfNotExists(event.getUser());
        database.createIfNotExists(event.getGuild());
        database.createIfNotExists(event.getMember());
    }
}
