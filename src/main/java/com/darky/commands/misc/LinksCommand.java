package com.darky.commands.misc;

import com.darky.core.caching.Cache;
import com.darky.util.DescriptionBuilder;
import com.darky.util.emotes.Emotes;
import com.darky.util.reactions.Builder;
import com.darky.util.reactions.Reactions;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.Set;

import static com.darky.core.Messages.editMessage;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class LinksCommand implements ICommand {

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {

        Reactions.newMenu(event, member.getUser(), channel, "Invite Links", "Here is the right place if you want some links!",
                new Builder(Reactions.ROBOT, Reactions.ROBOT, "Bot Invite", menu -> {
                    channel.getMessageById(menu.getMessageid()).queue( msg ->
                    editMessage(msg, event.getCache(), "Invite Links!",
                            "[Here](" + String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=2146958847", event.getJDA().getSelfUser().getIdLong())+")", member.getUser()).queue()
                    );
                }),
                new Builder(Emotes.getFromName("DiscordIcon"), "Support Server", menu -> {
                    channel.getMessageById(menu.getMessageid()).queue( msg ->
                            editMessage(msg, event.getCache(), "Invite Links!",
                                    "[Here](https://discord.gg/Q6tXZEp) is the link for the support server", member.getUser()).queue()
                    );
                }),
                new Builder(Emotes.getFromName("GitHub"), "Github organisation", menu -> {
                    channel.getMessageById(menu.getMessageid()).queue(msg ->
                            editMessage(msg, event.getCache(), "Invite Links!",
                                    "[Here](https://github.com/TheDiscordBot) is the link of the GitHub organisation", member.getUser()).queue()
                    );
                })
        );
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Cache cache) {
        return new DescriptionBuilder()
                .setColor(cache.getUser(member.getUser()).getEmbedcolor())
                .addUsage(prefix, labels, "", "Shows some useful links").build();
    }

    @Override
    public List<Permission> requiredPermissions() {
        return List.of(Permission.MESSAGE_MANAGE);
    }
}
