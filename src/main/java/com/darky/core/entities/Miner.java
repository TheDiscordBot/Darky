package com.darky.core.entities;

public class Miner {

    private long user_id;
    private long miner_id;
    private long minedcoins;
    private long chance;

    public Miner(long user_id, long minedcoins, long chance, long miner_id) {
        this.user_id = user_id;
        this.minedcoins = minedcoins;
        this.chance = chance;
        this.miner_id = miner_id;
    }

    public void setMiner_id(long miner_id) {
        this.miner_id = miner_id;
    }

    public long getMiner_id() {
        return miner_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
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
