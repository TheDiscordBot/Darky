package com.darky.util.emotes;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class Emotes {
    private static List<Emote> emotes;

    public static void init(String emotePath, Guild guild) throws IOException {
        var files = new File(emotePath).listFiles();

        assert files != null;
        for (var f : files) {
            if (!f.isDirectory()) {
                var name = f.getName().substring(0, f.getName().indexOf('.'));
                try {
                    if ((guild.getEmotesByName(name, true).size() == 0)) {
                        guild.getController().createEmote(name, Icon.from(f)).queue();
                    }
                } catch (NullPointerException e) {
                    guild.getController().createEmote(name, Icon.from(f)).queue();
                }
            }
        }
        emotes = new ArrayList<>();
        for (var emote : guild.getEmotes()) {
            emotes.add(new Emote(emote.getIdLong(), emote.getAsMention().replaceAll("(<)|(>)", ""),
                    emote.getAsMention(), emote.getName()));
        }
    }

    public static Emote getFromName(String name) {
        return emotes.stream().filter(e -> e.getName().equals(name)).collect(Collectors.toList()).get(0);
    }

    public static Emote getFromMention(String mention) {
        return emotes.stream().filter(e -> e.getAsMessage().equals(mention)).collect(Collectors.toList()).get(0);
    }

    public static Emote getFromReaction(String reaction) {
        return emotes.stream().filter(e -> e.getAsReaction().equals(reaction)).collect(Collectors.toList()).get(0);
    }
}
