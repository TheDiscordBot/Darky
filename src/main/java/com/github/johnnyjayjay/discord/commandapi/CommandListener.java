package com.github.johnnyjayjay.discord.commandapi;

import com.darky.core.Config;
import com.darky.core.Darky;
import com.darky.core.Database;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.darky.core.Messages.sendMessage;


class CommandListener extends ListenerAdapter {

    private Config config;
    private CommandSettings settings;
    private Map<Long, Long> cooldowns; // Long: User id, Long: last timestamp
    private Database database;

    public CommandListener(CommandSettings settings, Config config, Database database) {
        this.config = config;
        this.settings = settings;
        this.cooldowns = new HashMap<>();
        this.database = database;
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
                        if (hasPerms(event.getMember(), cmd)) {
                        if (event.getGuild().getSelfMember().hasPermission(cmd.getExecutor().requiredPermissions()) ||
                                event.getGuild().getSelfMember().hasPermission(event.getChannel(), cmd.getExecutor().requiredPermissions())) {
                            database.setCoins(event.getAuthor(), database.getCoins(event.getAuthor())+1);
                            cmd.getExecutor().onCommand(new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage(), cmd, settings, database),
                                    event.getMember(), channel, cmd.getArgs());
                        } else {
                            var desc = new StringBuilder().append("**__Required Permissions:__**\n\n");
                            for (var p: cmd.getExecutor().requiredPermissions()) {
                                if (!event.getGuild().getSelfMember().hasPermission(p)) {
                                    desc.append(p.getName()).append("\n");
                                }
                            }

                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(new Color(231,76,60))
                                    .setTitle("Missing permissions")
                                    .setDescription(desc.toString())
                                    .build()).queue();
                        }
                        } else sendMessage(database, channel, "Error!", "You haven't the permission to do that!", event.getAuthor()).queue();
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

    private boolean hasPerms(Member member, CommandEvent.Command cmd) {
        String perm = Darky.getPermission(cmd.getExecutor());
        System.out.println(perm);
        if (config.getOwnersAsList().contains(member.getUser().getIdLong())) return true;
        ArrayList<String> perms = new ArrayList<>(Arrays.asList(database.getPermissions(member)));
        String[] split = perm.split("\\.");
        if (split.length==2) if (perms.contains(split[0]+".*")) return true;
        if (perms.contains(perm) || perms.contains("*")) return true;
        return false;
    }
}
