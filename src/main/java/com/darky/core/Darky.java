package com.darky.core;

import com.darky.commands.moderation.BanCommand;
import com.darky.commands.user.*;
import com.darky.commands.misc.LinksCommand;
import com.darky.commands.moderation.KickCommand;
import com.darky.commands.owner.RegisterCommand;
import com.darky.listeners.DarkcoinListener;
import com.darky.listeners.MentionListener;
import com.darky.listeners.ReadyListener;
import com.darky.listeners.RegisterListener;
import com.github.johnnyjayjay.discord.commandapi.CommandSettings;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Darky extends ListenerAdapter {

    private ShardManager shardManager;
    private Config config;
    private Logger logger = LoggerFactory.getLogger(Darky.class);
    private Database database;
    private static ScheduledExecutorService executor;

    public static void main(String[] args) {
        new Darky().run();
    }

    private void run() {
        this.config = Config.loadConfig("config.json");
        this.database = new Database(config).connect();
        executor = Executors.newScheduledThreadPool(config.getThreadPool());

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(config.getToken())
                .setShardsTotal(config.getShards())
                .addEventListeners(new RegisterListener(database), new MentionListener(database), new DarkcoinListener(database), new ReadyListener(config));
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            logger.error("Error while building Shard Manager", e);
        }
        GameAnimator.start(shardManager);
        GitHub github = null;
        GHRepository repo = null;
        try {
            github = GitHub.connectUsingOAuth(config.getGithubtoken());
            GHOrganization org = github.getOrganization("TheDiscordBot");
            repo = org.getRepository("Darky");
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommandSettings settings = new CommandSettings(config.getPrefix(), shardManager, true, config, database);
        settings.put(new LinksCommand(), "links")
                .put(new BanCommand(), "ban")
                .put(new KickCommand(), "kick")
                .put(new RegisterCommand(), "register")
                .put(new HelpCommand(), "help", "helpme")
                .put(new MinerCommand(), "miner")
                .put(new PingCommand(), "ping")
                .put(new RepoCommand(repo), "repo")
                .put(new ProfileCommand(), "profile")
                .activate();
    }

    public static ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit timeUnit) {
        return executor.schedule(task, delay, timeUnit);
    }

    public static String getPermission(ICommand cmd) {
        String first = cmd.getClass().getPackageName().replace("com.darky.commands.", "");
        String second = cmd.getClass().getName().replace(cmd.getClass().getPackageName()+".", "").substring(0, 1).toLowerCase() +
                cmd.getClass().getName().replace(cmd.getClass().getPackageName()+".", "").substring(1).replace("Command", "");
        return first+"."+second;

    }
}
