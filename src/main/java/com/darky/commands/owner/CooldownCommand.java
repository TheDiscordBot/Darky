package com.darky.commands.owner;

import com.darky.core.Darky;
import com.github.johnnyjayjay.discord.commandapi.AbstractCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import com.github.johnnyjayjay.discord.commandapi.SubCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Map;

import static com.darky.core.Messages.sendMessage;

public class CooldownCommand extends AbstractCommand {

    @SubCommand
    public void onHelp(CommandEvent event, Member member, TextChannel channel, String[] args) {
        sendMessage(event.getDatabase(), event.getChannel(), "Help", "Use\nd!cooldown add @Member Time\nd!cooldown remove @Member").queue();
    }

    @SubCommand(args = {"add", MEMBER_MENTION}, moreArgs = true)
    public void onAdd(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (args.length==3) {
            Darky.getCooldownsPerUser().put(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong(), Long.parseLong(args[2]));
            sendMessage(event.getDatabase(), event.getChannel(), "Success!", "Added "+event.getMessage().getMentionedMembers().get(0).getUser().getName()+
                    " with "+args[2]+"ms Cooldown").queue();
        } else onHelp(event, member, channel, args);
    }

    @SubCommand(args = {"remove", MEMBER_MENTION}, moreArgs = true)
    public void onremove(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (args.length==3) {
            Darky.getCooldownsPerUser().remove(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong(), Long.parseLong(args[2]));
            sendMessage(event.getDatabase(), event.getChannel(), "Success!", "Removed "+event.getMessage().getMentionedMembers().get(0).getUser().getName()).queue();
        } else onHelp(event, member, channel, args);
    }
}
