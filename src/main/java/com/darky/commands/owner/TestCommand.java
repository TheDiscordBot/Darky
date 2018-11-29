package com.darky.commands.owner;

import com.darky.util.PageSwitcher;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;

public class TestCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(new MessageEmbed.Field("Test", "this is a test", false),
                new MessageEmbed.Field("Test1234523", "ölskjölkasjdf", false),
                new MessageEmbed.Field("Test123554", "ölskjölkasjdf", false),
                new MessageEmbed.Field("Test1298435769437653", "ölskjölkasjdf", false)));
        PageSwitcher.newPageSwitcher(event, channel, fields, "test", "test", 1);
    }
}
