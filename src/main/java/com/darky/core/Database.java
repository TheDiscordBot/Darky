package com.darky.core;

import com.darky.core.entities.Miner;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

public class Database {

    private Connection connection;
    private Config config;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Database(Config config) {
        this.config = config;
    }

    public Database connect() {
        logger.info("connecting to DB...");
        String sql = "CREATE DATABASE IF NOT EXISTS " + config.getDbName() + ";";
        try (Connection connection = DriverManager.getConnection(
                format("jdbc:mysql://%s:%s/?serverTimezone=UTC&useSSL=false", config.getDbHost(), config.getDbPort()), config.getDbUser(), config.getDbPw());
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            this.connection = DriverManager.getConnection(format("jdbc:mysql://%s:%s/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", config.getDbHost(), config.getDbPort(), config.getDbName()), config.getDbUser(), config.getDbPw());
        } catch (SQLException e) {
            logger.error("Exception caught while connecting", e);
            System.exit(1);
        }
        createTablesIfNotExist();
        logger.info("connected to DB!");
        return this;
    }

    public void createTablesIfNotExist() {
        try {
            for (String statement : Statements.createTables) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("Exception caught while creating tables", e);
            System.exit(1);
        }
    }

    public List<Miner> getAllMiners() {
        ArrayList<Miner> miners = new ArrayList<>();
        try (var statement = connection.prepareStatement("SELECT * FROM `Darkcoin`")) {
            var set = statement.executeQuery();
            while (set.next()) {
                miners.add(new Miner(set.getLong("user_id"), set.getLong("minedcoins"), set.getLong("chance"), set.getLong("miner_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return miners;
    }

    public List<Miner> getMinerfromUser(User user) {
        ArrayList<Miner> miners = (ArrayList<Miner>) this.getAllMiners();
        ArrayList<Miner> yourminers = new ArrayList<>();
        for (Miner miner:miners) {
            if (miner.getUserID()==user.getIdLong()) {
                yourminers.add(miner);
            }
        }
        return yourminers;
    }

    public void setMiner(Miner miner) {
        this.executeUpdate(Statements.updateMiner, miner.getMinedcoins(), miner.getChance(), miner.getMinerID());
    }

    public void insertMiner(long user_id) {
        this.executeUpdate(Statements.insertMiner, user_id);
    }

    public long getCreateTime(User user) {
        try (var statement = connection.prepareStatement("SELECT * FROM `Discord_user` WHERE user_id="+user.getIdLong()+";")) {
            var set = statement.executeQuery();
            if (set.next()) {
                return set.getLong("created");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void createIfNotExists(User user) {
        if (this.getFirst("entries", Statements.countUsers, Integer.TYPE, user.getIdLong()) == 0) {
            this.executeUpdate(Statements.insertUser, user.getIdLong(), System.currentTimeMillis());
        }
    }

    public void createIfNotExists(Guild guild) {
        if (this.getFirst("entries", Statements.countGuilds, Integer.TYPE, guild.getIdLong()) == 0) {
            this.executeUpdate(Statements.insertGuild, guild.getIdLong());
        }
    }

    public void createIfNotExists(Member member) {
        createIfNotExists(member.getUser());
        createIfNotExists(member.getGuild());
        if (this.getFirst("entries", Statements.countMembers, Integer.TYPE, member.getGuild().getIdLong(), member.getUser().getIdLong()) == 0) {
            this.executeUpdate(Statements.insertMember, member.getGuild().getIdLong(), member.getUser().getIdLong());
        }
    }

    public long getCoins(User user) {
        return this.getFirst("coins", Statements.selectFromUser, Long.TYPE, user.getIdLong());
    }

    public void setCoins(User user, long coins) {
        this.executeUpdate(Statements.setCoins, coins, user.getIdLong());
    }

    public String[] getPermissions(Member member) {
        var s = getFirst("permissions", Statements.selectFromMember, String.class, member.getUser().getIdLong(), member.getGuild().getIdLong());
        var split = s.split(", ");
        return split;
    }

    public List<String> getPermissionsAsList(Member member) {
        return Arrays.asList(getPermissions(member));
    }

    public void addPermissions(Member member, String... permissions) {
        var perms = Arrays.asList(this.getPermissions(member));
        var permsarraylist = new ArrayList<>(perms);
        Collections.addAll(permsarraylist, permissions);
        this.executeUpdate(Statements.updatePerms, Arrays.toString(permsarraylist.toArray()).replace("[", "").replace("]", ""), member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public void removePermissions(Member member, String... permissions) {
        var perms = Arrays.asList(this.getPermissions(member));
        var permsarraylist = new ArrayList<>(perms);
        permsarraylist.removeAll(Arrays.asList(permissions));
        this.executeUpdate(Statements.updatePerms, Arrays.toString(permsarraylist.toArray()).replace("[", "").replace("]", ""), member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public Color getColor(User user) {
        try {
            var s = getFirst("embedcolor", Statements.selectFromUser, String.class, user.getId());
            return hex2Rgb(s.startsWith("#") ? s : "#" + s);
        } catch (NullPointerException e) {
            return Color.BLACK;
        }
    }

    private static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    private <T> T getFirst(String column, String sql, Class<T> type, Object... args) {
        T t = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatement(args, statement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null && resultSet.next())
                t = resultSet.getObject(column, type);
        } catch (SQLException e) {
            logger.error("Exception while getting first query", e);
        }
        return t;
    }

    public Database executeUpdate(String sql, Object... args) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatement(args, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("An Exception occurred while execute an update", e);
        }
        return this;
    }

    private void setStatement(Object[] args, PreparedStatement statement) {
        try {
            for (int i = 0; i < args.length; i++) {
                Object current = args[i];
                logger.debug("Setting value for statement: " + current);
                if (current instanceof Short)
                    statement.setShort(i + 1, (short) current);
                else if (current instanceof Integer)
                    statement.setInt(i + 1, (int) current);
                else if (current instanceof Long)
                    statement.setLong(i + 1, (long) current);
                else if (current instanceof String)
                    statement.setString(i + 1, (String) current);
                else if (current instanceof Boolean)
                    statement.setBoolean(i + 1, (boolean) current);
            }
        } catch (SQLException e) {
            logger.error("An exception occurred while setting values for PreparedStatement", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private static class Statements {
        public static String[] createTables = {
                "CREATE TABLE IF NOT EXISTS Discord_guild (guild_id BIGINT NOT NULL,PRIMARY KEY (guild_id));",
                "CREATE TABLE IF NOT EXISTS Discord_user (user_id BIGINT NOT NULL,embedcolor VARCHAR(80) NOT NULL DEFAULT '#000000',coins BIGINT NOT NULL DEFAULT '100',created BIGINT NOT NULL,PRIMARY KEY (user_id));",
                "CREATE TABLE IF NOT EXISTS Discord_member (guild_id BIGINT NOT NULL,user_id BIGINT NOT NULL,permissions VARCHAR(80) NOT NULL DEFAULT 'user.*',UNIQUE (user_id, guild_id),FOREIGN KEY (guild_id) REFERENCES Discord_guild (guild_id)" +
                        " ON DELETE CASCADE,FOREIGN KEY (user_id) REFERENCES Discord_user (user_id));",
                "CREATE TABLE IF NOT EXISTS Darkcoin (user_id BIGINT NOT NULL,minedcoins BIGINT NOT NULL DEFAULT '0',chance BIGINT NOT NULL DEFAULT '1',miner_id BIGINT NOT NULL AUTO_INCREMENT," +
                        "FOREIGN KEY (user_id) REFERENCES Discord_user (user_id),PRIMARY KEY (miner_id));"
        };
        public static String selectFromUser = "SELECT * FROM Discord_user WHERE user_id = ?;";
        public static String selectFromMember = "SELECT * FROM Discord_member WHERE user_id = ? AND guild_id = ?;";
        public static String insertUser = "INSERT INTO Discord_user (user_id, created) VALUES (?, ?);";
        public static String insertGuild = "INSERT INTO Discord_guild (guild_id) VALUE (?);";
        public static String insertMember = "INSERT INTO Discord_member(guild_id, user_id) VALUES (?, ?);";
        public static String countUsers = "SELECT COUNT(*) AS entries FROM Discord_user WHERE user_id = ?;";
        public static String countGuilds = "SELECT COUNT(*) AS entries FROM Discord_guild WHERE guild_id = ?;";
        public static String countMembers = "SELECT COUNT(*) AS entries FROM Discord_member WHERE guild_id = ? AND user_id = ?;";
        public static String updatePerms = "UPDATE Discord_member SET permissions = ? WHERE guild_id = ? AND user_id = ?;";
        public static String updateMiner = "UPDATE Darkcoin SET minedcoins=?, chance=? WHERE miner_id = ?;";
        public static String insertMiner = "INSERT INTO Darkcoin (user_id) VALUES (?);";
        public static String setCoins = "UPDATE Discord_user SET coins = ? WHERE user_id = ?;";
    }
}
