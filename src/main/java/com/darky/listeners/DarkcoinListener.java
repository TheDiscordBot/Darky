package com.darky.listeners;

import com.darky.core.caching.Cache;
import com.darky.core.caching.Darkcoin;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DarkcoinListener extends ListenerAdapter {

    private Cache cache;

    public DarkcoinListener(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void onReady(ReadyEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<Darkcoin> darkcoins = new ArrayList<>(cache.getMiners().values());
                for (Darkcoin darkcoin : darkcoins) {
                    darkcoin.setMinedcoins(darkcoin.getMinedcoins()+(darkcoin.getChance()));
                }
            }
        }, 1000, 1000);
    }
}
