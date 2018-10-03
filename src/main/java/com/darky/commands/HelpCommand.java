package com.darky.commands;

import com.darky.core.Database;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.AbstractHelpCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.darky.core.Messages.sendMessage;


/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class HelpCommand extends AbstractHelpCommand {

    private Database database;

    public HelpCommand(Database database) {
        this.database = database;
    }

    @Override
    public void provideGeneralHelp(CommandEvent event, String prefix, Map<String, ICommand> commands) {
        var embed = new EmbedBuilder().setColor(new Color(52, 73, 94)).setDescription("For more information about a command use " + event.getCommandSettings().getPrefix(event.getGuild().getIdLong()) + "help <Command>\n\n");

        Set<String> categories = new HashSet<>();
        commands.forEach((label, cmd) -> {
            if (cmd.getClass().getPackage() == this.getClass().getPackage())
                categories.add("No Category");
            else
                categories.add(cmd.getClass().getPackage().getName().replace("com.darky.commands.", ""));
        });

        categories.forEach(c -> {
            var list = new HashMap<ICommand, String>();

            commands.forEach((l, cmd) -> {
                if (getCategory(cmd).equals(c) && !list.containsKey(cmd))
                    list.put(cmd, "`" + l + "`");
            });

            embed.addField(firstLetterUpperCase(c), String.join(",", list.values()), false);
        });

        sendMessage(database, event.getChannel(),null, null, event.getAuthor(), false, null, embed).queue();

    }

    @Override
    public void provideSpecificHelp(CommandEvent event, String prefix, ICommand command, Set<String> labels) {
        event.respond(command.info(event.getMember(), prefix, labels));
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return new DescriptionBuilder().addUsage(prefix, labels, "", "Shows the help message")
                .addUsage(prefix, labels, "<command>", "Shows more information about the command")
                .build();
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
