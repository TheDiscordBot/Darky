package com.github.johnnyjayjay.discord.commandapi;

import com.darky.core.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;


class CommandListener extends ListenerAdapter {

    private Config config;
    private CommandSettings settings;
    private Map<Long, Long> cooldowns; // Long: User id, Long: last timestamp

    public CommandListener(CommandSettings settings, Config config) {
        this.config = config;
        this.settings = settings;
        this.cooldowns = new HashMap<>();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        if (!settings.getBlacklistedChannels().contains(channel.getIdLong()) && (!event.getAuthor().isBot() || settings.botsMayExecute())) {
            String raw = event.getMessage().getContentRaw();
            String prefix = settings.getPrefix(event.getGuild().getIdLong());
            if (raw.startsWith(prefix)) {
                long timestamp = System.currentTimeMillis();
                long userId = event.getAuthor().getIdLong();
                if (cooldowns.containsKey(userId) && (timestamp - cooldowns.get(userId)) < settings.getCooldown()) {
                    if (settings.isResetCooldown())
                        cooldowns.put(userId, timestamp);
                    return;
                }
                cooldowns.put(userId, timestamp);
                CommandEvent.Command cmd = CommandEvent.parseCommand(raw, prefix, settings);
                if (cmd.getExecutor() != null) {
                    try {
                        cmd.getExecutor().onCommand(new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage(), cmd, settings),
                                event.getMember(), channel, cmd.getArgs());
                    } catch (Throwable t) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription("An unknown error occurred. The developers will try to fix it").build()).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

                        event.getJDA().getTextChannelById(config.getErrorChannel()).sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("**__User:__** " + event.getMember().getAsMention() + " `(" + event.getMember().getUser().getId() + ")`\n" +
                                        "**__Guild:__** " + event.getGuild().getName() + " `(" + event.getGuild().getId() + ")`\n" +
                                        "**__Command:__** " + cmd.getLabel())
                                .addField("Error", "```css\n" + t.toString() + "\n```", false)
                                .addField("Stacktrace", String.join("\n", Arrays.toString(Arrays.copyOfRange(t.getStackTrace(), 0, 10))), false).build()).queue();
                    }
                } else {
                    Message unknownCommand = settings.getUnknownCommandMessage();
                    if (unknownCommand != null && event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS))
                        channel.sendMessage(unknownCommand).queue();
                }
            }
        }
    }
}
