package com.darky.core;

import com.darky.commands.HelpCommand;
import com.darky.commands.owner.registerCommand;
import com.darky.listeners.RegisterListener;
import com.github.johnnyjayjay.discord.commandapi.CommandSettings;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Darky {

    private ShardManager shardManager;
    private Config config;
    private Logger logger = LoggerFactory.getLogger(Darky.class);
    private Database database;

    public static void main(String[] args) {
        new Darky().run();
    }

    private void run() {
        this.config = Config.loadConfig("config.json");
        this.database = new Database(config).connect();

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(config.getToken())
                .setShardsTotal(config.getShards())
                .setGame(Game.playing("with bulby"));
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            logger.error("Error while building Shard Manager", e);
        }
        shardManager.addEventListener(new RegisterListener(database));
        CommandSettings settings = new CommandSettings("d!", shardManager, true);
                settings.put(new HelpCommand(database), "help", "helpme")
                        .put(new registerCommand(database), "register")
                .activate();

        logger.info("Bot successfully started!");
    }

}
