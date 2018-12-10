package com.darky.commands.user;

import com.darky.core.caching.Cache;
import com.darky.core.caching.Darkcoin;
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

import static com.darky.core.Darky.getHelp;
import static com.darky.core.Messages.sendMessage;

public class MinerCommand extends AbstractCommand {

    @SubCommand
    public void onFalseUsage(CommandEvent event, Member member, TextChannel channel, String[] args) {
        getHelp(event);
    }

    @SubCommand(args = "buy", moreArgs = true)
    public void onMinerBuy(CommandEvent event, Member member, TextChannel channel, String[] args) {
        sendMessage(event.getCache(), event.getChannel(), "Error!", "This command is now named buy :wink:", event.getAuthor()).queue();
    }

    @SubCommand(args = "list")
    public void onMinerList(CommandEvent event, Member member, TextChannel channel, String[] args) {
        ArrayList<Darkcoin> darkcoins = new ArrayList<>(event.getCache().getMiners().values());
        ArrayList<Darkcoin> yourminers = new ArrayList<>();
        for (Darkcoin darkcoin : darkcoins) {
            if (darkcoin.getUser_id()==member.getUser().getIdLong()) {
                yourminers.add(darkcoin);
            }
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        for (int i = 0; yourminers.size()>i; i++) {
            Darkcoin darkcoin = yourminers.get(i);
            embedBuilder.addField("Miner "+(i+1), "Coins Per Second: "+ darkcoin.getChance()+"\nCoins: "+ darkcoin.getMinedcoins()+"\nMiner ID: "+ darkcoin.getMiner_id(), true);
        }
        sendMessage(event.getCache(), channel, "Miner Info", "Here are your miners!", member.getUser(), false, null, embedBuilder).queue();
    }

    @SubCommand(args = "transfer")
    public void onMinerTransferAll(CommandEvent event, Member member, TextChannel channel, String[] args) {
        long coins = 0;
        for (Darkcoin darkcoin :event.getCache().getMinerFromUser(event.getAuthor().getIdLong())) {
            coins+= darkcoin.getMinedcoins();
            darkcoin.setMinedcoins(0L);
        }
        event.getCache().getUser(event.getAuthor()).setCoins(event.getCache().getUser(event.getAuthor()).getCoins()+coins);
        sendMessage(event.getCache(), channel, "Success!", "Sent "+coins+" Coins to your Account!", member.getUser(), true).queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Cache cache) {
        return new DescriptionBuilder()
                .setColor(cache.getUser(member.getUser()).getEmbedcolor())
                .addUsage(prefix, labels, "list", "List your Miners")
                .addUsage(prefix, labels, "transfer", "Transfer money to your Account")
                .build();
    }
}
