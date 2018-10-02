package com.darky.core;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
class Config {

    @SerializedName("TOKEN")
    private String token;
    @SerializedName("SHARDS")
    private int shards;
    @SerializedName("DB_HOST")
    private String db_host;
    @SerializedName("DB_PORT")
    private String db_port;
    @SerializedName("DB_NAME")
    private String db_name;
    @SerializedName("DB_USER")
    private String db_user;
    @SerializedName("DB_PW")
    private String db_pw;

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private String configPath;
    private Gson gson;

    private Config(String configPath) {
        this.configPath = configPath;
        this.gson = new Gson();
    }

    public static Config loadConfig(String path) {
        var config = new Config(path);
        if (!Files.exists(Paths.get(path)))
            config.createConfig();
        try {
            config = config.gson.fromJson(new String(Files.readAllBytes(Paths.get(path))), config.getClass());
            logger.debug("Config successfully loaded!");
        } catch (IOException e) {
            logger.error("Error while loading config", e);
            System.exit(1);
        }
        return config;
    }

    private void createConfig() {
        if (!Files.exists(Paths.get(this.configPath))) {
            try {
                Files.createFile(Paths.get(this.configPath));
            } catch (IOException e) {
                logger.error("Error while creating config", e);
                System.exit(1);
            }
        }
    }

    public String getToken() {
        return token;
    }

    public int getShards() {
        return shards;
    }

    public String getDb_host() {
        return db_host;
    }

    public String getDb_user() {
        return db_user;
    }

    public String getDb_pw() {
        return db_pw;
    }

    public String getDb_name() {
        return db_name;
    }

    public String getDb_port() {
        return db_port;
    }
}
