package com.darky.core;

import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;

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
        String sql = "CREATE DATABASE IF NOT EXISTS " + config.getDb_name() + ";";
        try (Connection connection = DriverManager.getConnection(
                format("jdbc:mysql://%s:%s/?serverTimezone=UTC&useSSL=false", config.getDb_host(), config.getDb_port()), config.getDb_user(), config.getDb_pw());
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            this.connection = DriverManager.getConnection(format("jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", config.getDb_host(), config.getDb_port(), config.getDb_name()), config.getDb_user(), config.getDb_pw());
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

    public Color getColor(User user) {
        return new Color(getFirst("embedcolor", Statements.getColor, Integer.class, user.getIdLong()));
    }

    private <T> T getFirst(String column, String sql, Class<T> type, Object... args) {
        T t = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatement(args, statement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null && resultSet.next())
                t = resultSet.getObject(column, type);
        } catch (SQLException e) {
            logger.error("Exception while getting first query",e);
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
                "CREATE TABLE IF NOT EXISTS Discord_user (user_id BIGINT NOT NULL,embedcolor VARCHAR(8),PRIMARY KEY (user_id));",
                "CREATE TABLE IF NOT EXISTS Discord_member (member_id BIGINT NOT NULL AUTO_INCREMENT, guild_id BIGINT NOT NULL,user_id BIGINT NOT NULL," +
                    "UNIQUE (user_id, guild_id),FOREIGN KEY (guild_id) REFERENCES Discord_guild (guild_id) ON DELETE CASCADE,FOREIGN KEY (user_id) " +
                    "REFERENCES Discord_user (user_id),PRIMARY KEY (member_id));"
        };
        public static String getColor = "SELECT * FROM `Discord_user` WHERE `user_id`=?;";
    }
}
