package com.darky.commands;

import com.github.johnnyjayjay.discord.commandapi.AbstractHelpCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.*;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class HelpCommand extends AbstractHelpCommand {
    @Override
    public void provideGeneralHelp(CommandEvent event, String prefix, Map<String, ICommand> commands) {
        var embed = new EmbedBuilder().setTitle("HelpCommand").setColor(new Color(52, 73, 94)).setDescription("For more information about a command use " + event.getCommandSettings().getPrefix(event.getGuild().getIdLong()) + "help <Command>\n\n");

        Set<String> categories = new HashSet<>();
        commands.forEach((label, cmd) -> {
            if (cmd.getClass().getPackage() == this.getClass().getPackage())
                categories.add("No Category");
            else
                categories.add(cmd.getClass().getPackage().getName().replace("com.darky.commands.", ""));
        });

        categories.forEach(c -> {
            var list = new ArrayList<String>();

            commands.forEach((l, cmd) -> {
                if (getCategory(cmd).equals(c))
                    list.add("`" + l + "`");
            });

            embed.addField(firstLetterUpperCase(c), String.join(",", list), false);
        });

        event.respond(embed.build());
    }

    @Override
    public void provideSpecificHelp(CommandEvent event, String prefix, ICommand command, Set<String> labels) {
        event.respond(command.info(event.getMember(), prefix, labels));
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return new MessageBuilder(new EmbedBuilder().setColor(new Color(45, 52, 54)).setTitle(this.getClass().getName().replaceFirst("com.darky.commands.", "")).setDescription(
                "**__Usage:__**\n\n" +
                        prefix + "[" + String.join("|", labels) + "] **Shows this message**\n" +
                        prefix + "[" + String.join("|", labels) + "] <Command> **More information about a command**"
        ).build()).build();
    }

    private String firstLetterUpperCase(String inp) {
        return inp.substring(0, 1).toUpperCase() + inp.substring(1);
    }

    private String getCategory(ICommand cmd) {
        if (cmd.getClass().getPackage() == this.getClass().getPackage())
            return "No Category";
        else
            return cmd.getClass().getPackage().getName().replace("com.darky.commands.", "");
    }
}