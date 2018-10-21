package com.darky.util.reactions;

import java.util.HashMap;
import java.util.function.Consumer;

public class Menu {
    private Long userid;
    private Long messageid;
    private HashMap<String, Consumer<Menu>> actions; // Emote name, Action

    public Menu(Long userid, Long messageid, HashMap<String, Consumer<Menu>> actions) {
        this.userid = userid;
        this.messageid = messageid;
        this.actions = actions;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getMessageid() {
        return messageid;
    }

    public void setMessageid(Long messageid) {
        this.messageid = messageid;
    }

    public HashMap<String, Consumer<Menu>> getActions() {
        return actions;
    }

    public void setActions(HashMap<String, Consumer<Menu>> actions) {
        this.actions = actions;
    }
}
