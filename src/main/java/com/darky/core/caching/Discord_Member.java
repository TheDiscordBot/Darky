package com.darky.core.caching;

import java.util.Arrays;
import java.util.List;

public class Discord_Member {

    public Long guild_id;
    public Long user_id;
    public String permissions;

    public Discord_Member(Long guild_id, Long user_id, String permissions) {
        this.guild_id = guild_id;
        this.user_id = user_id;
        this.permissions = permissions;
    }

    public Long getGuild_id() {
        return guild_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getPermissions() {
        return permissions;
    }

    public List<String> getPermissionsAsList() {
        return Arrays.asList(permissions.split(", "));
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Discord_User getUser(Cache cache) {
        return cache.getUser(user_id);
    }

    public Discord_Guild getGuild(Cache cache) {
        return cache.getGuild(guild_id);
    }
}
