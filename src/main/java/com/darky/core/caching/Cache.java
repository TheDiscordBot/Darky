package com.darky.core.caching;

import com.darky.core.Database;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Cache {

    private Database database;
    private HashMap<String, Darkcoin> miners; // Miner_id, miner
    private HashMap<String, Discord_Guild> discord_guilds; // guild_id, guild
    private HashMap<String, Discord_User> discord_users; // user_id, user
    private HashMap<String, Discord_Member> discord_members; // user_id+""+guild_id, member

    public Cache(Database database) {
        this.database = database;
    }

    public void scan(Consumer success) {
        this.scan();
        success.accept(null);
    }

    public void scan() {
        discord_guilds = (HashMap<String, Discord_Guild>) database.getEntities(Discord_Guild.class);
        discord_users = (HashMap<String, Discord_User>) database.getEntities(Discord_User.class);
        discord_members = (HashMap<String, Discord_Member>) database.getEntities(Discord_Member.class);
        miners = (HashMap<String, Darkcoin>) database.getEntities(Darkcoin.class);
    }

    public void write(Consumer success) {
        this.write();
        success.accept(null);
    }

    public void write() {
        database.setEntities(Discord_User.class, discord_users);
        database.setEntities(Discord_Guild.class, discord_guilds);
        database.setEntities(Discord_Member.class, discord_members);
        database.setEntities(Darkcoin.class, miners);
    }

    public Discord_User getUser(User user) {
        return discord_users.get(user.getId());
    }

    public Discord_User getUser(Long user_id) {
        return discord_users.get(String.valueOf(user_id));
    }

    public Discord_Member getMember(Member member) {
        return discord_members.get(member.getUser().getId()+""+member.getGuild().getId());
    }

    public Discord_Member getMember(Long user_id, Long guild_id) {
        return discord_members.get(user_id+""+guild_id);
    }

    public Discord_Guild getGuild(Guild guild) {
        return discord_guilds.get(guild.getId());
    }

    public Discord_Guild getGuild(Long guild_id) {
        return discord_guilds.get(String.valueOf(guild_id));
    }

    public Darkcoin getMiner(Long miner_id) {
        return miners.get(miner_id);
    }

    public void createIfNotExist(Member member) {
        this.createIfNotExist(member.getUser());
        this.createIfNotExist(member.getGuild());
        if (!discord_members.containsKey(member.getUser().getId()+member.getGuild().getId())) {
            discord_members.put(member.getUser().getId()+member.getGuild().getId(), new Discord_Member(member.getGuild().getIdLong(), member.getUser().getIdLong(), "user.*"));
        }
    }

    public void createIfNotExist(User user) {
        if (!discord_users.containsKey(user.getId())) {
            discord_users.put(user.getId(), new Discord_User(user.getIdLong(), "#000000", 100L, System.currentTimeMillis()));
        }
    }

    public void createIfNotExist(Guild guild) {
        if (!discord_guilds.containsKey(guild.getId())) {
            discord_guilds.put(guild.getId(), new Discord_Guild(guild.getIdLong()));
        }
    }

    public void insertMiner(Long user_id) {
        boolean hasID = false;
        while (!hasID) {
            Random r = new Random();
            Long test = r.nextLong();
            if (!miners.containsKey(String.valueOf(test))) {
                hasID = true;
                miners.put(String.valueOf(test), new Darkcoin(user_id, 0L, 1L, test));
            }
        }
    }

    public List<Darkcoin> getMinerFromUser(Long user_id) {
        List<Darkcoin> darkcoinList = new ArrayList<>();
        for (Darkcoin darkcoin :miners.values()) {
            if (darkcoin.getUser_id()==user_id) {
                darkcoinList.add(darkcoin);
            }
        }
        return darkcoinList;
    }

    public HashMap<String, Darkcoin> getMiners() {
        return miners;
    }

    public HashMap<String, Discord_Guild> getDiscord_guilds() {
        return discord_guilds;
    }

    public HashMap<String, Discord_User> getDiscord_users() {
        return discord_users;
    }

    public HashMap<String, Discord_Member> getDiscord_members() {
        return discord_members;
    }
}
