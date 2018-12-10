package com.darky.commands.moderation;

import com.darky.core.caching.Cache;
import com.darky.core.caching.Discord_Member;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.AbstractCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.SubCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

import static com.darky.core.Darky.getHelp;
import static com.darky.core.Messages.sendMessage;

public class PermissionCommand extends AbstractCommand {

    @SubCommand(args = {"add", MEMBER_MENTION}, moreArgs = true)
    public void onAddPermission(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (member.isOwner()||member.hasPermission(Permission.ADMINISTRATOR)) {
            member = event.getMessage().getMentionedMembers().get(0);
            Discord_Member discord_member = event.getCache().getMember(event.getMember());
            if (!discord_member.getPermissionsAsList().contains(args[2])) {
                discord_member.getPermissionsAsList().add(args[2]);
                sendMessage(event.getCache(), event.getChannel(), "Success!", "Successfully added the permission " + args[2], event.getAuthor()).queue();
            } else sendMessage(event.getCache(), event.getChannel(), "Error!", "The member have the Permission already", event.getAuthor()).queue();
        }
    }

    @SubCommand(args = {"remove", MEMBER_MENTION}, moreArgs = true)
    public void onRemovePermission(CommandEvent event, Member member, TextChannel channel, String[] args) {
        if (member.hasPermission(Permission.ADMINISTRATOR)||member.isOwner()) {
            Discord_Member discord_member = event.getCache().getMember(event.getMember());
            if (discord_member.getPermissionsAsList().contains(args[2])) {
                discord_member.getPermissionsAsList().remove(args[2]);
                sendMessage(event.getCache(), event.getChannel(), "Success!", "Successfully removed the permission " + args[2], event.getAuthor()).queue();
            } else sendMessage(event.getCache(), event.getChannel(), "Error!", "The member don't have the Permission...", event.getAuthor()).queue();
        }
    }

    @SubCommand(args = {"get", MEMBER_MENTION})
    public void onMemberGetPermissions(CommandEvent event, Member member, TextChannel channel, String[] args) {
        sendMessage(event.getCache(), event.getChannel(), "Permissions from "+event.getMessage().getMentionedMembers().get(0).getEffectiveName(), "``"+event.getCache().getMember(event.getMessage().getMentionedMembers().get(0)).getPermissionsAsList().toString()+"``", event.getAuthor()).queue();
    }

    @SubCommand
    public void onHelp(CommandEvent event, Member member, TextChannel channel, String[] args) {
        getHelp(event);
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Cache cache) {
        return new DescriptionBuilder()
                .setColor(cache.getUser(member.getUser()).getEmbedcolor())
                .addUsage(prefix, labels, "add @Member *Permission*", "Add a permission to a member")
                .addUsage(prefix, labels, "remove @Member *Permission*", "Remove a permission to a member")
                .addUsage(prefix, labels, "get @Member", "get the permissions from a member")
                .build();
    }
}
