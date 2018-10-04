package com.darky.commands.misc;

import com.darky.core.Database;
import com.darky.util.DescriptionBuilder;
import com.darky.util.Reactions;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

import static com.darky.core.Messages.editMessage;
import static com.darky.core.Messages.sendMessage;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class InviteCommand implements ICommand {
    private Reactions reactions;
    private Database database;

    public InviteCommand(Reactions reactions, Database database) {
        this.reactions = reactions;
        this.database = database;
    }

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        sendMessage(database, channel, "Invite Links").queue(msg -> {
            reactions.newMenu(member.getUser(), msg, Set.of(Reactions.NO_EMOTE, Reactions.YES_EMOTE), (emote, u) -> {
                editMessage(msg, database, null, emote + " TEST").queue();
            }, (v) -> msg.delete().queue(), 30, true);
        });
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return new DescriptionBuilder().addUsage(prefix, labels, "", "Shows some invite links").build();
    }
}
