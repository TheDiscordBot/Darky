package com.darky.commands.moderation;

import com.darky.core.Database;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.Set;

/**
 * https://github.com/Stupremee
 * @author Stu
 */
public class KickCommand implements ICommand {
    private Database database;

    public KickCommand(Database database) {
        this.database = database;
    }

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) {
        channel.sendMessage("The user got kicked successfully!").queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return new DescriptionBuilder()
                .setColor(database.getColor(member.getUser()))
                .addUsage(prefix, labels, "@Member *Reason*", "Kicks the Member")
                .addPermission("mod.kick")
                .build();
    }

    @Override
    public List<Permission> requiredPermissions() {
        return List.of(Permission.KICK_MEMBERS);
    }
}
