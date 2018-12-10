package com.darky.commands.user;

import com.darky.core.caching.Cache;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

import static com.darky.core.Messages.sendMessage;

public class PingCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        sendMessage(event.getCache(), channel, "Ping", "Current Ping: "+event.getJDA().getPing(), member.getUser()).queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Cache cache) {
        return new DescriptionBuilder()
                .setColor(cache.getUser(member.getUser()).getEmbedcolor())
                .addUsage(prefix, labels, "", "Shows the Bots Ping")
                .build();
    }

}
