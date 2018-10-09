package com.darky.util.emotes;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class Emote {
    private long id;
    private String reactionString;
    private String messageString;
    private String name;

    public Emote(long id, String reactionString, String messageString, String name) {
        this.id = id;
        this.reactionString = reactionString;
        this.messageString = messageString;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public long getId() {
        return id;
    }
    public String getAsMessage() {
        return messageString;
    }
    public String getAsReaction() {
        return reactionString;
    }
}
