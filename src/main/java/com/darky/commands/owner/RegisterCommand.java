package com.darky.commands.owner;

import com.darky.core.Database;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

import static com.darky.core.Messages.editMessage;
import static com.darky.core.Messages.sendMessage;


public class RegisterCommand implements ICommand {

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        sendMessage(event.getDatabase(), channel, "Registering all Users and Guilds...", null, event.getAuthor()).queue(m -> {
            for (Guild guild : event.getJDA().asBot().getShardManager().getGuilds()) {
                for (Member member1 : guild.getMembers()) {
                    event.getDatabase().createIfNotExists(member1);
                }
            }
            editMessage(m, event.getDatabase(), "Finished!", null, event.getAuthor(), false, null, null).queue();
        });
    }
}
