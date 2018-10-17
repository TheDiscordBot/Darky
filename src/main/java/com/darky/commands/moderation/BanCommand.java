package com.darky.commands.moderation;

import com.darky.core.Database;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.xml.crypto.Data;

import static com.darky.core.Messages.sendMessage;

public class BanCommand implements ICommand {

    private Database database;

    public BanCommand(Database database) {
        this.database = database;
    }

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        if (event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            if (member.hasPermission(Permission.BAN_MEMBERS)) {
                if (event.getMessage().getMentionedMembers().size() == 1) {
                    event.getGuild().getController().ban(event.getMessage().getMentionedMembers().get(0).getUser(), 0).queue(
                            msg -> sendMessage(database, channel, "Success", "Banned!", member.getUser())
                    );
                } else sendMessage(database, channel, "Error!", "False usage... use d!help ban", member.getUser());
            } else
                sendMessage(database, channel, "Error!", "You hasn't the permission to do this", member.getUser());
        } else sendMessage(database, channel, "Error!", "I haven't the permission to do this", member.getUser());
    }

    @Override
    public String permission() {
        return "mod.ban";
    }
}
