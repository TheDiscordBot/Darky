package com.darky.commands;

import com.darky.core.Database;
import com.github.johnnyjayjay.discord.commandapi.AbstractCommand;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.SubCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

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
}
