package com.darky.core;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Database {

    private Connection connection;
    private Config config;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Database(Config config) {
        this.config = config;
    }

    public Database connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+config.getDb_host()+"/?serverTimezone=UTC", config.getDb_user(), config.getDb_pw());
        } catch (SQLException e) {
            logger.error("An Error occurred while connecting to the Database... ", e);
            logger.error("Exiting...");
            System.exit(1);
        }
        return this;
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
}
