package com.darky.util.reactions;

import com.darky.util.emotes.Emote;

import java.util.function.Consumer;

public class Builder {
    private String text;
    private String textEmote;
    private String emote;
    private Consumer<Menu> action;

    public Builder(String emote, Consumer<Menu> action) {
        this.emote = emote;
        this.action = action;
    }

    public Builder(String emote, String textEmote, String text, Consumer<Menu> action) {
        this.text = text;
        this.textEmote = textEmote;
        this.emote = emote;
        this.action = action;
    }

    public Builder(Emote emote, String text, Consumer<Menu> action) {
        this.text = text;
        this.textEmote = emote.getAsMessage();
        this.emote = emote.getAsReaction();
        this.action = action;
    }

    public Builder(String emote, String text, Consumer<Menu> action) {
        this.text = text;
        this.textEmote = emote;
        this.emote = emote;
        this.action = action;
    }

    public String getText() {
        return text;
    }

    public String getEmote() {
        return emote;
    }

    public Consumer<Menu> getAction() {
        return action;
    }

    public String getTextEmote() {
        return textEmote;
    }
}
