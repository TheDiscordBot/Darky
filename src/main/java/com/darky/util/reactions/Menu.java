package com.darky.util.reactions;

import net.dv8tion.jda.core.entities.User;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
class Menu {
    public Long[] user;
    public long message;
    public Collection<String> emotes;
    public BiConsumer<String, User> reacted;
    public Consumer<Void> aborted;
    public int duration;
    public boolean remove;

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