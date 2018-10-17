package com.darky.commands.user;

import com.darky.core.Database;
import com.darky.core.entities.Miner;
import com.github.johnnyjayjay.discord.commandapi.AbstractCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.SubCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;

import static com.darky.core.Messages.sendMessage;

public class MinerCommand extends AbstractCommand {
    private Database database;

    public MinerCommand(Database database) {
        this.database = database;
    }

    @SubCommand(args = "buy")
    public void onMinerBuy(CommandEvent event, Member member, TextChannel channel, String[] args) {
        database.insertMiner(member.getUser().getIdLong());
        sendMessage(database, channel, "Success!", "I buyed your miner!", member.getUser(), true).queue();
    }

    @SubCommand(args = "list")
    public void onMinerList(CommandEvent event, Member member, TextChannel channel, String[] args) {
        ArrayList<Miner> miners = (ArrayList<Miner>) database.getAllMiners();
        ArrayList<Miner> yourminers = new ArrayList<>();
        for (Miner miner:miners) {
            if (miner.getUser_id()==member.getUser().getIdLong()) {
                yourminers.add(miner);
            }
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        for (int i = 0; yourminers.size()>i; i++) {
            Miner miner = yourminers.get(i);
            embedBuilder.addField("Miner "+(i+1), "Chance: "+miner.getChance()+"\nCoins: "+miner.getMinedcoins()+"\nMiner ID: "+miner.getMiner_id(), true);
        }
        sendMessage(database, channel, "Miner Info", "Here are your miners!", member.getUser(), false, null, embedBuilder).queue();
    }

    @SubCommand(args = "transfer")
    public void onMinerTransferAll(CommandEvent event, Member member, TextChannel channel, String[] args) {
        ArrayList<Miner> miners = (ArrayList<Miner>) database.getAllMiners();
        ArrayList<Miner> yourminers = new ArrayList<>();
        for (Miner miner:miners) {
            if (miner.getUser_id()==member.getUser().getIdLong()) {
                yourminers.add(miner);
            }
        }
        long coins = 0;
        for (Miner miner:yourminers) {
            coins+=miner.getMinedcoins();
            miner.setMinedcoins(0);
            database.setMiner(miner);
        }
        database.setCoins(member.getUser(), database.getCoins(member.getUser())+coins);
        sendMessage(database, channel, "Success!", "Sent "+coins+" Coins to your Account!", member.getUser(), true).queue();
    }

    @Override
    public String permission() {
        return "user.miner";
    }
}
