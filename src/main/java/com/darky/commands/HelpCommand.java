package com.darky.commands;

import com.github.johnnyjayjay.discord.commandapi.AbstractHelpCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;

import java.util.Map;
import java.util.Set;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class HelpCommand extends AbstractHelpCommand {
    @Override
    public void provideGeneralHelp(CommandEvent event, String prefix, Map<String, ICommand> commands) {
        var builder = new StringBuilder();
        commands.forEach((label, cmd) -> {
            
        });
    }

    @Override
    public void provideSpecificHelp(CommandEvent event, String prefix, ICommand command, Set<String> labels) {

    }
}
