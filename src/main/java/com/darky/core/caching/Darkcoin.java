package com.darky.core.caching;

public class Darkcoin {

    public Long user_id;
    public Long minedcoins;
    public Long chance;
    public Long miner_id;

    public Darkcoin(Long user_id, Long minedcoins, Long chance, Long miner_id) {
        this.user_id = user_id;
        this.minedcoins = minedcoins;
        this.chance = chance;
        this.miner_id = miner_id;
    }

    public void setMiner_id(Long miner_id) {
        this.miner_id = miner_id;
    }

    public void setMinedcoins(Long minedcoins) {
        this.minedcoins = minedcoins;
    }

    public void setChance(Long chance) {
        this.chance = chance;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Long getMinedcoins() {
        return minedcoins;
    }

    public Long getChance() {
        return chance;
    }

    public Long getMiner_id() {
        return miner_id;
    }
}
