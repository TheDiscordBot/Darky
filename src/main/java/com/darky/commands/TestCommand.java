package com.darky.commands;

import com.darky.util.reactions.Reactions;
import com.darky.util.reactions.Builder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class TestCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        Reactions.newMenu(event, member.getUser(), channel, "test", "Wähl aus ey",
                new Builder("DAS IST EIN TEST", Reactions.NO_EMOTE, test -> event.getChannel().sendMessage("NO").queue()),
                new Builder("DAS IST EIN FUCKING TEST EY", Reactions.YES_EMOTE, test -> event.getChannel().sendMessage("YES").queue())
        );
    }

    @Override
    public String permission() {
        return null;
    }
}
