package com.darky.core;

import com.darky.commands.HelpCommand;
import com.darky.commands.misc.LinksCommand;
import com.darky.commands.moderation.KickCommand;
import com.darky.commands.owner.RegisterCommand;
import com.darky.listeners.MentionListener;
import com.darky.listeners.RegisterListener;
import com.darky.util.emotes.Emotes;
import com.darky.util.Reactions;
import com.github.johnnyjayjay.discord.commandapi.CommandSettings;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Darky extends ListenerAdapter {

    private ShardManager shardManager;
    private Config config;
    private Reactions reactions;
    private Logger logger = LoggerFactory.getLogger(Darky.class);
    private Database database;
    private static ScheduledExecutorService executor;

    public static void main(String[] args) {
        new Darky().run();
    }

    private void run() {
        this.reactions = new Reactions();
        this.config = Config.loadConfig("config.json");
        this.database = new Database(config).connect();
        executor = Executors.newScheduledThreadPool(config.getThreadPool());

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(config.getToken())
                .setShardsTotal(config.getShards())
                .setGame(Game.playing("with bulby"));
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            logger.error("Error while building Shard Manager", e);
        }

        shardManager.addEventListener(new RegisterListener(database), new MentionListener(database, shardManager), this.reactions, this);
        CommandSettings settings = new CommandSettings(config.getPrefix(), shardManager, true, config);
        settings.put(new HelpCommand(database), "help", "helpme")
                .put(new KickCommand(database), "kick")
                .put(new RegisterCommand(database), "register")
                .put(new LinksCommand(reactions, database), "links")
                .activate();
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            Emotes.init("./emotes", event.getJDA().getGuildById(config.getEmoteGuild()));
            logger.info("Emotes successfully uploaded");
        } catch (IOException e) {
            logger.error("Error while uploading emotes", e);
        }
        logger.info("Bot successfully started! " + event.getJDA().getShardInfo().getShardTotal() + " Shards are online");
    }

    public static ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit timeUnit) {
        return executor.schedule(task, delay, timeUnit);
    }
}
