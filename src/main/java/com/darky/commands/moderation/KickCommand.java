package com.darky.commands.moderation;

import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

/**
 * https://github.com/Stupremee
 * @author Stu
 */
public class KickCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) {
        channel.sendMessage("The user got kicked successfully!").queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return new DescriptionBuilder()
                .addUsage(prefix, labels, "@Member *Reason*", "Kicks the Member")
                .addPermission("mod.kick")
                .build();
    }
}
