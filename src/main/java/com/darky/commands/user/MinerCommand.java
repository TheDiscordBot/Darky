package com.darky.commands.user;

import com.darky.core.Database;
import com.darky.core.entities.Miner;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.AbstractCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.SubCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Set;

import static com.darky.core.Messages.sendMessage;

public class MinerCommand extends AbstractCommand {

    @SubCommand
    public void onFalseUsage(CommandEvent event, Member member, TextChannel channel, String[] args) {
        sendMessage(event.getDatabase(), channel, "Error!", "False usage... use d!help miner", member.getUser()).queue();
    }

    @SubCommand(args = "buy", moreArgs = true)
    public void onMinerBuy(CommandEvent event, Member member, TextChannel channel, String[] args) {
        sendMessage(event.getDatabase(), event.getChannel(), "Error!", "This command is now named buy :wink:", event.getAuthor()).queue();
    }

    @SubCommand(args = "list")
    public void onMinerList(CommandEvent event, Member member, TextChannel channel, String[] args) {
        ArrayList<Miner> miners = (ArrayList<Miner>) event.getDatabase().getAllMiners();
        ArrayList<Miner> yourminers = new ArrayList<>();
        for (Miner miner:miners) {
            if (miner.getUserID()==member.getUser().getIdLong()) {
                yourminers.add(miner);
            }
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        for (int i = 0; yourminers.size()>i; i++) {
            Miner miner = yourminers.get(i);
            embedBuilder.addField("Miner "+(i+1), "Chance: "+miner.getChance()+"\nCoins: "+miner.getMinedcoins()+"\nMiner ID: "+miner.getMinerID(), true);
        }
        sendMessage(event.getDatabase(), channel, "Miner Info", "Here are your miners!", member.getUser(), false, null, embedBuilder).queue();
    }

    @SubCommand(args = "transfer")
    public void onMinerTransferAll(CommandEvent event, Member member, TextChannel channel, String[] args) {
        ArrayList<Miner> miners = (ArrayList<Miner>) event.getDatabase().getAllMiners();
        ArrayList<Miner> yourminers = new ArrayList<>();
        for (Miner miner:miners) {
            if (miner.getUserID()==member.getUser().getIdLong()) {
                yourminers.add(miner);
            }
        }
        long coins = 0;
        for (Miner miner:yourminers) {
            coins+=miner.getMinedcoins();
            miner.setMinedcoins(0);
            event.getDatabase().setMiner(miner);
        }
        event.getDatabase().setCoins(member.getUser(), event.getDatabase().getCoins(member.getUser())+coins);
        sendMessage(event.getDatabase(), channel, "Success!", "Sent "+coins+" Coins to your Account!", member.getUser(), true).queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Database database) {
        return new DescriptionBuilder()
                .setColor(database.getColor(member.getUser()))
                .addUsage(prefix, labels, "list", "List your Miners")
                .addUsage(prefix, labels, "transfer", "Transfer money to your Account")
                .build();
    }
}
