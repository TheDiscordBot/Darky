package com.darky.commands.owner;

import com.darky.core.Darky;
import com.github.johnnyjayjay.discord.commandapi.AbstractCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.SubCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import static com.darky.core.Messages.sendMessage;

public class BlacklistCommand extends AbstractCommand {

    @SubCommand
    public void onHelp(CommandEvent event, Member member, TextChannel channel, String[] args) {
        sendMessage(event.getCache(), event.getChannel(), "Help", "Use\nd!blacklist add @Member\nd!blacklist remove @Member").queue();
    }

    @SubCommand(args = {"add", MEMBER_MENTION}, moreArgs = true)
    public void onAdd(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (args.length==2) {
            Darky.getBlacklist().add(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());
            sendMessage(event.getCache(), event.getChannel(), "Success!", "Added "+event.getMessage().getMentionedMembers().get(0).getUser().getName()+" to the blacklist!").queue();
        } else onHelp(event, member, channel, args);
    }

    @SubCommand(args = {"remove", MEMBER_MENTION}, moreArgs = true)
    public void onremove(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (args.length==2) {
            Darky.getBlacklist().remove(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());
            sendMessage(event.getCache(), event.getChannel(), "Success!", "Removed "+event.getMessage().getMentionedMembers().get(0).getUser().getName()+" from the blacklist!").queue();
        } else onHelp(event, member, channel, args);
    }

}
