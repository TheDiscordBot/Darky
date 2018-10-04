package com.darky.util;

import com.darky.core.Darky;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class Reactions extends ListenerAdapter {
    public static final String YES_EMOTE = "✅";
    public static final String NO_EMOTE = "❌";


    private List<Menu> menus;
    private boolean firstReaction = true;

    public Reactions() {
        this.menus = new ArrayList<>();
    }

    /*public void newMenu(User[] user, Message msg, Collection<String> emotes, BiConsumer<String, User> reacted, Consumer<Void> aborted, int duration, boolean remove) {
    }*/

    public void newMenu(User user, Message msg, Collection<String> emotes, BiConsumer<String, User> reacted, Consumer<Void> aborted, int duration, boolean remove) {
        emotes.forEach(e -> msg.addReaction(e).queue());
        menus.add(new Menu(new Long[]{user.getIdLong()}, msg.getIdLong(), emotes, reacted, aborted, duration, remove));
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (menus.stream().noneMatch(m -> m.message == event.getMessageIdLong()))
            return;
        var menu = menus.stream().filter(m -> m.message == event.getMessageIdLong()).collect(Collectors.toList()).get(0);

        if (event.getUser() == event.getJDA().getSelfUser())
            return;

        if (firstReaction && menu.remove) {
            Darky.scheduleTask(() -> {
                menu.aborted.accept(null);
                menus.remove(menu);
            }, menu.duration, TimeUnit.SECONDS);
            firstReaction = false;
        }

        var emote = event.getReactionEmote().getName();
        if (Arrays.asList(menu.user).contains(event.getUser().getIdLong()) && menu.emotes.contains(emote)) {
            menu.reacted.accept(emote, event.getUser());
            event.getReaction().removeReaction(event.getUser()).queue();
        } else {
            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }

    private class Menu {
        private Long[] user;
        private long message;
        private Collection<String> emotes;
        private BiConsumer<String, User> reacted;
        private Consumer<Void> aborted;
        private int duration;
        private boolean remove;

        Menu(Long[] user, long message, Collection<String> emotes, BiConsumer<String, User> reacted, Consumer<Void> aborted, int duration, boolean remove) {
            this.user = user;
            this.message = message;
            this.emotes = emotes;
            this.reacted = reacted;
            this.aborted = aborted;
            this.duration = duration;
            this.remove = remove;
        }
    }
}
