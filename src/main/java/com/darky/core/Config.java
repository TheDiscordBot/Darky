package com.darky.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * https://github.com/Stupremee
 * @author: Stu
 */
public class Config {

    @SerializedName("THREAD_POOL")
    @Expose
    private int threadPool;
    @SerializedName("ERROR_CHANNEL")
    @Expose
    private long errorChannel;
    @SerializedName("PREFIX")
    @Expose
    private String prefix;
    @SerializedName("TOKEN")
    @Expose
    private String token;
    @SerializedName("SHARDS")
    @Expose
    private int shards;
    @SerializedName("DB_HOST")
    @Expose
    private String db_host;
    @SerializedName("DB_PORT")
    @Expose
    private String db_port;
    @SerializedName("DB_NAME")
    @Expose
    private String db_name;
    @SerializedName("DB_USER")
    @Expose
    private String db_user;
    @SerializedName("DB_PW")
    @Expose
    private String db_pw;

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private String configPath;
    private Gson gson;

    private Config(String configPath) {
        this.configPath = configPath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation().create();
    }

    static Config loadConfig(String path) {
        var config = new Config(path);
        if (!Files.exists(Paths.get(path))) {
            config.createConfig();
            return null;
        } else {
            try {
                config = config.gson.fromJson(new String(Files.readAllBytes(Paths.get(path))), config.getClass());
                if (config.verifyConfig()) {
                    logger.debug("Config successfully loaded!");
                    return config;
                } else {
                    logger.error("Something in your config is not filled up correctly (Shards count can not be 0)");
                    System.exit(1);
                    return null;
                }
            } catch (IOException e) {
                logger.error("Error while loading config", e);
                System.exit(1);
                return null;
            }
        }
    }

    private void setDefaultValues() {
        this.db_host = "Database host";
        this.db_name = "Database name";
        this.db_port = "Database port";
        this.db_pw = "Database password";
        this.db_user = "Database user";
        this.shards = 1;
        this.token = "Your token";
        this.errorChannel = 0;
    }

    private void createConfig() {
        if (!Files.exists(Paths.get(this.configPath))) {
            try {
                this.setDefaultValues();
                var f = Files.createFile(Paths.get(this.configPath));
                var json = gson.toJson(this, Config.class);
                Files.write(f, json.getBytes());
                logger.info("Please fill out the config and restart the Bot!");
                System.exit(1);
            } catch (IOException e) {
                logger.error("Error while creating config", e);
                System.exit(1);
            }
        }
    }

    private boolean verifyConfig() {
        return !(this.token == null || this.shards == 0 || this.db_user == null || this.db_pw == null ||
                this.db_port == null || this.db_name == null || this.db_host == null || this.prefix == null || this.errorChannel == 0 ||
                this.threadPool == 0);
    }

    public String getPrefix() {
        return prefix;
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

    public long getErrorChannel() {
        return errorChannel;
    }

    public int getThreadPool() {
        return threadPool;
    }
}
