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

    private Database database;

    public RegisterCommand(Database database) {
        this.database = database;
    }

    @Override
    public void onCommand(CommandEvent commandEvent, Member member, TextChannel textChannel, String[] strings) throws Exception {
        sendMessage(database, textChannel, "Registering all Users and Guilds...", null, commandEvent.getAuthor()).queue(m -> {
            for (Guild guild : commandEvent.getJDA().asBot().getShardManager().getGuilds()) {
                for (Member member1 : guild.getMembers()) {
                    database.createIfNotExists(member1);
                }
            }
            editMessage(m, database, "Finished!", null, commandEvent.getAuthor(), false, null, null).queue();
        });
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return null;
    }
}
