package com.darky.core.caching;

import java.awt.*;

public class Discord_User {

    public Long user_id;
    public String embedcolor;
    public Long coins;
    public Long created;

    public Discord_User(Long user_id, String embedcolor, Long coins, Long created) {
        this.user_id = user_id;
        this.embedcolor = embedcolor;
        this.coins = coins;
        this.created = created;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Color getEmbedcolor() {
        return new Color(
                Integer.valueOf(embedcolor.substring(1, 3), 16),
                Integer.valueOf(embedcolor.substring(3, 5), 16),
                Integer.valueOf(embedcolor.substring(5, 7), 16));
    }

    public Long getCoins() {
        return coins;
    }

    public Long getCreated() {
        return created;
    }

    public void setEmbedcolor(String embedcolor) {
        this.embedcolor = embedcolor;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public void setCreated(Long created) {
        this.created = created;
    }
}
