package com.darky.commands.user;

import com.darky.core.caching.Cache;
import com.darky.core.caching.Discord_User;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

import static com.darky.core.Darky.getHelp;
import static com.darky.core.Messages.sendMessage;

public class BuyCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        if (args.length>0) {
            Discord_User user = event.getCache().getUser(member.getUser());
            switch (args[0].toLowerCase()) {
                case "miner":
                    if (args.length == 2) {
                        int coins = 100 * Integer.parseInt(args[1]);
                        if (user.getCoins() >= coins) {
                            for (int i = 0; Integer.parseInt(args[1]) > i; i++) {
                                event.getCache().insertMiner(member.getUser().getIdLong());
                            }
                            user.setCoins(user.getCoins() - coins);
                            sendMessage(event.getCache(), channel, "Success!", "I bought you " + args[1] + " miner!", member.getUser(), true).queue();
                        } else
                            sendMessage(event.getCache(), event.getChannel(), "Error!", "You haven't enough coins!", event.getAuthor()).queue();
                    } else {
                        if (user.getCoins() >= 100) {
                            event.getCache().insertMiner(member.getUser().getIdLong());
                            sendMessage(event.getCache(), channel, "Success!", "I bought you a miner!", member.getUser(), true).queue();
                            user.setCoins(user.getCoins() - 100);
                        } else
                            sendMessage(event.getCache(), event.getChannel(), "Error!", "You haven't enough coins!", event.getAuthor()).queue();
                    }
                    break;
            }
        } else getHelp(event);
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Cache cache) {
        return new DescriptionBuilder()
                .setColor(cache.getUser(member.getUser()).getEmbedcolor())
                .addUsage(prefix, labels, "miner [how many]", "Buys you Miner")
                .build();
    }
}
