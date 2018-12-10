package com.darky.commands.owner;

import com.darky.core.Messages;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;


public class CacheCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        if (args.length==0) {
            Messages.sendMessage(event.getCache(), channel, "Error", "use\n\ncache update\ncache read", event.getAuthor()).queue();
        } else if (args[0].equals("update")) {
            Messages.sendMessage(event.getCache(), channel, "Working...", "Updating...", event.getAuthor(), true).queue(
                msg -> event.getCache().write(
                        msg2 -> Messages.editMessage(msg, event.getCache(), "Success!", "Updated!", event.getAuthor(), true).queue()
                )
            );
        } else if (args[0].equals("read")) {
            Messages.sendMessage(event.getCache(), channel, "Working...", "reading...", event.getAuthor(), true).queue(
                    msg -> event.getCache().scan(
                            msg2 -> Messages.editMessage(msg, event.getCache(), "Success!", "Read the DB!", event.getAuthor(), true).queue()
                    )
            );
        }
    }
}
