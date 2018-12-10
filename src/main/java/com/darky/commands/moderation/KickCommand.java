package com.darky.commands.moderation;

import com.darky.commands.user.HelpCommand;
import com.darky.core.caching.Cache;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.Set;

import static com.darky.core.Darky.getHelp;
import static com.darky.core.Messages.sendMessage;

/**
 * https://github.com/Stupremee
 * @author Stu
 */
public class KickCommand implements ICommand {

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
            if (member.hasPermission(Permission.KICK_MEMBERS)) {
                if (event.getMessage().getMentionedMembers().size() == 1) {
                    if (event.getGuild().getSelfMember().canInteract(event.getMessage().getMentionedMembers().get(0))) {
                        event.getGuild().getController().kick(event.getMessage().getMentionedMembers().get(0)).queue(
                                msg -> sendMessage(event.getCache(), channel, "Success", "Kicked!", member.getUser()).queue()
                        );
                    } else sendMessage(event.getCache(), channel, "Error!", "I haven't the permission to do this", member.getUser()).queue();
                } else getHelp(event);
            } else
                sendMessage(event.getCache(), channel, "Error!", "You hasn't the permission to do this", member.getUser()).queue();
        } else sendMessage(event.getCache(), channel, "Error!", "I haven't the permission to do this", member.getUser()).queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Cache cache) {
        return new DescriptionBuilder()
                .setColor(cache.getUser(member.getUser()).getEmbedcolor())
                .addUsage(prefix, labels, "@Member *Reason*", "Kicks the Member")
                .build();
    }

    @Override
    public List<Permission> requiredPermissions() {
        return List.of(Permission.KICK_MEMBERS);
    }
}
