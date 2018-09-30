package com.darky.core;

import com.github.johnnyjayjay.discord.commandapi.CommandSettings;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Darky {

    private ShardManager shardManager;
    private JSONObject jsonObject;

    public static void main(String[] args) {
        new Darky();
    }

    public Darky() {
        try {
            jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
        builder.setToken(jsonObject.getString("TOKEN"));
        builder.setShardsTotal(jsonObject.getInt("SHARDS"));
        builder.setGame(Game.playing("with bulby"));
        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        CommandSettings settings = new CommandSettings("d!", shardManager, true);
    }

}
