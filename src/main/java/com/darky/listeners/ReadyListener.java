package com.darky.listeners;

import com.darky.core.Config;
import com.darky.util.emotes.Emotes;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReadyListener extends ListenerAdapter {

    Logger logger = LoggerFactory.getLogger("Darky-ReadyListener");

    private Config config;

    public ReadyListener(Config config) {
        this.config = config;
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
}
