package com.darky.core.entities;

public class Miner {

    private long userID;
    private long minerID;
    private long minedcoins;
    private long chance;

    public Miner(long userID, long minerID, long minedcoins, long chance) {
        this.userID = userID;
        this.minerID = minerID;
        this.minedcoins = minedcoins;
        this.chance = chance;
    }

    public long getUserID() {
        return userID;
    }

    public long getMinerID() {
        return minerID;
    }

    public long getMinedcoins() {
        return minedcoins;
    }

    public void setMinedcoins(long minedcoins) {
        this.minedcoins = minedcoins;
    }

    public long getChance() {
        return chance;
    }

    public void setChance(long chance) {
        this.chance = chance;
    }
}
