package com.darky.commands.user;

import com.darky.core.Database;
import com.darky.core.entities.Miner;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

import static com.darky.core.Messages.sendMessage;

public class BuyCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        if (args.length>0) {
            switch (args[0].toLowerCase()) {
                case "miner":
                    if (args.length == 2) {
                        int coins = 100 * Integer.parseInt(args[1]);
                        if (event.getDatabase().getCoins(event.getAuthor()) >= coins) {
                            for (int i = 0; Integer.parseInt(args[1]) > i; i++) {
                                event.getDatabase().insertMiner(member.getUser().getIdLong());
                            }
                            event.getDatabase().setCoins(event.getAuthor(), event.getDatabase().getCoins(event.getAuthor()) - coins);
                            sendMessage(event.getDatabase(), channel, "Success!", "I bought you " + args[1] + " miner!", member.getUser(), true).queue();
                        } else
                            sendMessage(event.getDatabase(), event.getChannel(), "Error!", "You haven't enough coins!", event.getAuthor()).queue();
                    } else {
                        if (event.getDatabase().getCoins(event.getAuthor()) >= 100) {
                            event.getDatabase().insertMiner(member.getUser().getIdLong());
                            sendMessage(event.getDatabase(), channel, "Success!", "I bought you a miner!", member.getUser(), true).queue();
                            event.getDatabase().setCoins(event.getAuthor(), event.getDatabase().getCoins(event.getAuthor()) - 100);
                        } else
                            sendMessage(event.getDatabase(), event.getChannel(), "Error!", "You haven't enough coins!", event.getAuthor()).queue();
                    }
                    break;

                case "chance":
                    if (args.length<2) sendMessage(event.getDatabase(), channel, "Error!", "False usage... use d!help buy", member.getUser()).queue();
                    int minerid = Integer.parseInt(args[1]);
                    int coins = 100;
                    int many = 1;
                    if (args.length == 3) {
                        many = Integer.parseInt(args[2]);
                    }
                    coins = many * coins;
                    if (event.getDatabase().getCoins(event.getAuthor()) >= coins) {
                        Miner miner = event.getDatabase().getMinerfromMinerID(minerid);
                        miner.setChance(miner.getChance() + many);
                        event.getDatabase().setMiner(miner);
                        event.getDatabase().setCoins(event.getAuthor(), event.getDatabase().getCoins(event.getAuthor()) - coins);
                        sendMessage(event.getDatabase(), channel, "Success!", "I bought you " + many + " Chances!", member.getUser(), true).queue();
                    } else
                        sendMessage(event.getDatabase(), event.getChannel(), "Error!", "You haven't enough coins!", event.getAuthor()).queue();
                    break;
            }
        } else sendMessage(event.getDatabase(), channel, "Error!", "False usage... use d!help buy", member.getUser()).queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Database database) {
        return new DescriptionBuilder()
                .setColor(database.getColor(member.getUser()))
                .addUsage(prefix, labels, "miner [how many]", "Buys you Miner")
                .addUsage(prefix, labels, "chance (minerid) [how many]", "Buys you Chances")
                .build();
    }
}
