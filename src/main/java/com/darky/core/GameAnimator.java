package com.darky.core;

import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.Arrays;

public class GameAnimator {

    private static final ArrayList<Game> games = new ArrayList<>(
            Arrays.asList(
                    Game.playing("with Bulby..."),
                    Game.listening("commands..."),
                    Game.streaming("your data to the DB...", "https://www.twitch.tv/darky_devs"),
                    Game.watching("on all these Users..."),
                    Game.playing("on Discord!"),
                    Game.listening("events..."),
                    Game.watching("on all these Guilds...")
            )
    );

    public static void start(ShardManager shardManager) {
        new Thread(() -> {
            for (Game game:games) {
                try {
                    shardManager.setGame(game);
                    Thread.sleep(25000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
