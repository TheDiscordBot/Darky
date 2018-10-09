package com.darky.listeners;

import com.darky.core.Darky;
import com.darky.core.Database;
import com.darky.core.entities.Miner;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DarkcoinListener extends ListenerAdapter {

    private Database database;

    public DarkcoinListener(Database database) {
        this.database = database;
    }

    @Override
    public void onReady(ReadyEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<Miner> miners = (ArrayList<Miner>) database.getAllMiners();
                ArrayList<Miner> minerswithchance = new ArrayList<>();
                for (Miner miner : miners) {
                    for (int i=0; miner.getChance()>i; i++) {
                        minerswithchance.add(miner);
                    }
                }
                if (minerswithchance.size()>0) {
                    Miner miner = minerswithchance.get(new Random().nextInt(minerswithchance.size()));
                    miner.setMinedcoins(miner.getMinedcoins() + 1);
                    database.setMiner(miner);
                }
            }
        }, 1000, 1000);
    }
}
