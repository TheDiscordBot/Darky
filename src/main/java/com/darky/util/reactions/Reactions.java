package com.darky.util.reactions;

import com.darky.core.Darky;
import com.darky.util.emotes.Emotes;
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
    public static final String ROBOT = "\uD83E\uDD16";

    private static List<Menu> menus;
    private boolean firstReaction = true;

    public Reactions() {
        menus = new ArrayList<>();
    }

    public static void newYesNoMenu(User[] user, Message msg, Consumer<User> yes, Consumer<User> no, Consumer<Void> aborted, int duration, boolean remove) {
        menus.add(new Menu(Arrays.stream(user).map(User::getIdLong).toArray(Long[]::new), msg.getIdLong(), List.of(Reactions.YES_EMOTE, Reactions.NO_EMOTE), (emote, u) -> {
            if (emote.equals(Reactions.YES_EMOTE))
                yes.accept(u);
            else if (emote.equals(Reactions.NO_EMOTE))
                no.accept(u);
        }, aborted, duration, remove));
    }

    public static void newYesNoMenu(User user, Message msg, Consumer<User> yes, Consumer<User> no, Consumer<Void> aborted, int duration, boolean remove) {
        menus.add(new Menu(new Long[]{user.getIdLong()}, msg.getIdLong(), List.of(Reactions.YES_EMOTE, Reactions.NO_EMOTE), (emote, u) -> {
            if (emote.equals(Reactions.YES_EMOTE))
                yes.accept(u);
            else if (emote.equals(Reactions.NO_EMOTE))
                no.accept(u);
        }, aborted, duration, remove));
    }

    public static void newMenu(User[] user, Message msg, Collection<String> emotes, BiConsumer<String, User> reacted, Consumer<Void> aborted, int duration, boolean remove) {
        emotes.forEach(e -> msg.addReaction(e).queue());
        menus.add(new Menu(Arrays.stream(user).map(User::getIdLong).toArray(Long[]::new), msg.getIdLong(), emotes, reacted, aborted, duration, remove));
    }

    public static void newMenu(User user, Message msg, Collection<String> emotes, BiConsumer<String, User> reacted, Consumer<Void> aborted, int duration, boolean remove) {
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

        String emote;
        if (event.getReactionEmote().getEmote() == null)
            emote = event.getReactionEmote().getName();
        else
            emote = Emotes.getFromMention(event.getReactionEmote().getEmote().getAsMention()).getAsReaction();

        if (Arrays.asList(menu.user).contains(event.getUser().getIdLong()) && menu.emotes.contains(emote)) {
            menu.reacted.accept(emote, event.getUser());
            event.getReaction().removeReaction(event.getUser()).queue();
        } else {
            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }
}
