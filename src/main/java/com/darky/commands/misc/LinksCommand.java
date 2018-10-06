package com.darky.commands.misc;

import com.darky.core.Database;
import com.darky.util.DescriptionBuilder;
import com.darky.util.Reactions;
import com.darky.util.emotes.Emote;
import com.darky.util.emotes.Emotes;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.Set;

import static com.darky.core.Messages.editMessage;
import static com.darky.core.Messages.sendMessage;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class LinksCommand implements ICommand {
    private Reactions reactions;
    private Database database;

    public LinksCommand(Reactions reactions, Database database) {
        this.reactions = reactions;
        this.database = database;
    }

    @Override // https://discord.gg/Q6tXZEp
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        sendMessage(database, channel, "Invite Links", "Here is the right place if you want some links!\n\n" +
                "React with:\n" + Emotes.getFromName("DiscordIcon").getAsMessage() + " to get a invite to the support server\n" +
                Emotes.getFromName("GitHub").getAsMessage() + " to get the GitHub organisation link\n" +
                Reactions.ROBOT + " to get the Bot Invite\n", member.getUser(), true, null, new EmbedBuilder().setFooter("You have 30 seconds to react", null)).queue(msg -> {
            reactions.newMenu(member.getUser(), msg, List.of(Emotes.getFromName("DiscordIcon").getAsReaction(), Emotes.getFromName("GitHub").getAsReaction(), Reactions.ROBOT), (emote, user) -> {
                if (emote.equals(Emotes.getFromName("DiscordIcon").getAsReaction())){
                    editMessage(msg, database, "Invite Links!", "[Here](https://discord.gg/Q6tXZEp) is the link for the support server", member.getUser()).queue();
                    msg.clearReactions().queue();
                }
                else if (emote.equals(Emotes.getFromName("GitHub").getAsReaction())){
                    editMessage(msg, database, "Invite Links!", "[Here](https://github.com/TheDiscordBot) is the link of the GitHub organisation", member.getUser()).queue();
                    msg.clearReactions().queue();
                }
                else if (emote.equals(Reactions.ROBOT)){
                    editMessage(msg, database, "Invite Links!", "[Here](" + String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=2146958847", event.getJDA().getSelfUser().getId()) + ") is the invite link for the bot", member.getUser()).queue();
                    msg.clearReactions().queue();
                }
            }, v -> {
            }, 30, true);
        });
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels) {
        return new DescriptionBuilder()
                .setColor(database.getColor(member.getUser()))
                .addUsage(prefix, labels, "", "Shows some useful links").build();
    }
}
