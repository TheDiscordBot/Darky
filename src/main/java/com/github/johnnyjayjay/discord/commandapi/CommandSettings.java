package com.github.johnnyjayjay.discord.commandapi;

import com.darky.core.Config;
import com.darky.core.Database;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * To use this framework, create a new object of this class and add your command classes by using add(...)<p>
 * When you want your commands to become active, use activate()
 * @author Johnny_JayJay
 * @version 3.2
 * @since 1.1
 */
public class CommandSettings {

    /**
     * A regex that only matches valid prefixes. Can be used to check user input.
     */
    public static final String VALID_PREFIX = "[^\\\\+*^|$?]+";
    /**
     * A regex that only matches valid labels. Can be used to check user input.
     */
    public static final String VALID_LABEL = "[^\\s]+";

    // TODO: 05.08.2018 illegal prefix characters

    /**
     * The logger of this framework. This is protected.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger("CommandAPI");
    
    private final String INVALID_PREFIX_MESSAGE = "Prefix cannot be empty or contain the characters +*^|$\\?";
    private final String INVALID_LABEL_MESSAGE = "Label cannot be empty, consist of multiple words or contain new lines!";

    private Message unknownCommandMessage;
    private String defaultPrefix;
    private long cooldown;
    private Color helpColor;

    private final Set<Long> blacklistedChannels; // ids of those channels where no command will trigger this api to execute anything.
    @Deprecated
    private Set<String> helpLabels; // labels which trigger the auto-generated help command
    private final Map<Long, String> prefixMap; // Long: GuildID, String: prefix

    private final Map<String, ICommand> commands; // String: command label, ICommand: command class

    private Object jda; // The JDA or ShardManager

    private final CommandListener listener;

    private boolean activated; // ...is this instance activated?
    private boolean useShardManager;

    private boolean labelIgnoreCase;
    private boolean resetCooldown;
    private boolean botExecution;


    /**
     * This is the optional constructor in case you are sharding your bot.
     * The parameters are validated automatically. In case of any problems (if the prefix is empty), this will throw a CommandSetException.
     * @param shardManager Put your active ShardManager here. This is important for the activation of the CommandListener.
     * @param defaultPrefix The String you will have to put before every command in order to get your command execution registered. This can later be changed.
     * @param labelIgnoreCase Set this to true, if you want deactivate case sensitivity for the recognition of labels. E.g.: there will be no difference between the labels "foo",
     *                       "FOO", "FoO" and so on.
     */
    public CommandSettings(@Nonnull String defaultPrefix, @Nonnull ShardManager shardManager, boolean labelIgnoreCase, Config config, Database database) {
        this(defaultPrefix, labelIgnoreCase, config, database);
        this.jda = shardManager;
        this.useShardManager = true;
    }

    /**
     * This is the constructor.
     * The parameters are validated automatically. In case of any problems (if the prefix is empty), this will throw a CommandSetException.
     * @param jda Put your active JDA here. This is important for the activation of the CommandListener.
     * @param defaultPrefix The String you will have to put before every command in order to get your command execution registered. This can later be changed.
     * @param labelIgnoreCase Set this to true, if you want deactivate case sensitivity for the recognition of labels. E.g.: there will be no difference between the labels "foo",
     *                        "FOO", "FoO" and so on.
     */
    public CommandSettings(@Nonnull String defaultPrefix, @Nonnull JDA jda, boolean labelIgnoreCase, Config config, Database database) {
        this(defaultPrefix, labelIgnoreCase, config, database);
        this.jda = jda;
        this.useShardManager = false;
    }

    private CommandSettings(@Nonnull String defaultPrefix, boolean labelIgnoreCase, Config config, Database database) {
        this.commands = new HashMap<>();
        this.listener = new CommandListener(this, config, database);
        this.activated = false;
        this.cooldown = 0;
        this.helpColor = null;
        this.botExecution = false;
        this.setDefaultPrefix(defaultPrefix);
        this.labelIgnoreCase = labelIgnoreCase;
        this.resetCooldown = false;
        this.helpLabels = new HashSet<>();
        this.blacklistedChannels = new HashSet<>();
        this.prefixMap = new HashMap<>();
    }

    /**
     * Method to add one help label.
     * @param label The label to add.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException if the given label is invalid (contains spaces)
     * @deprecated This method is deprecated and thus not supported anymore. Help Commands are now registered like any other command,
     * i.e. using CommandSettings#put. To use the default implementation of the help command, register an instance of DefaultHelpCommand.
     * @see DefaultHelpCommand
     */
    @Deprecated
    public CommandSettings addHelpLabel(String label) {
        if (label.matches(VALID_LABEL))
            this.helpLabels.add(labelIgnoreCase ? label.toLowerCase() : label);
        else
            throw new CommandSetException(INVALID_LABEL_MESSAGE);

        return this;
    }

    /**
     * Use this method to add help labels. This will only work if you instantiated this class with the parameter useHelpCommand as true.
     * @param labels One or more labels which may later be called by members to list all commands or to show info about one specific command.
     * @return The current object. This is to use fluent interface.
     * @deprecated This method is deprecated and thus not supported anymore. Help Commands are now registered like any other command,
     * i.e. using CommandSettings#put. To use the default implementation of the help command, register an instance of DefaultHelpCommand.
     * @see DefaultHelpCommand
     */
    @Deprecated
    public CommandSettings addHelpLabels(@Nonnull String... labels) {
        for (String label : labels)
            this.addHelpLabel(label);
        return this;
    }

    /**
     * Adds multiple labels from a String Set.
     * @param labels A Set which contains the labels you want to add.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException if one of the labels is not a valid label.
     * @deprecated  This method is deprecated and thus not supported anymore. Help Commands are now registered like any other command,
     * i.e. using CommandSettings#put. To use the default implementation of the help command, register an instance of DefaultHelpCommand.
     * @see DefaultHelpCommand
     */
    @Deprecated
    public CommandSettings addHelpLabels(@Nonnull Collection<String> labels) {
        this.helpLabels.addAll(labelIgnoreCase ? labels.stream().map(String::toLowerCase).collect(Collectors.toList()) : labels);
        return this;
    }

    /**
     * This method removes one specific help label from the help label Set.
     * @param label The label to remove.
     * @return true, if the label was successfully removed. False, if not.
     * @deprecated This method is deprecated and thus not supported anymore. Help commands are now removed like any other command,
     * using CommandSettings#remove.
     */
    @Deprecated
    public boolean removeHelpLabel(String label) {
        return this.helpLabels.remove(labelIgnoreCase ? label.toLowerCase() : label);
    }

    /**
     * This can be used to remove some help labels, but not all of them.
     * @param labels The help labels to remove.
     * @return true, if every label was successfully removed. false, if one of the given labels does not exist and thus was not removed.
     * @deprecated This method is deprecated and thus not supported anymore. Help commands are now removed like any other command,
     * using CommandSettings#remove.
     */
    @Deprecated
    public boolean removeHelpLabels(@Nonnull String... labels) {
        boolean success = true;
        for (String label : labels) {
            if (!this.removeHelpLabel(label)) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Removes all labels from a Set.
     * @param labels The Set of labels that are to be removed.
     * @return true, if every label was successfully removed. false, if one of the given labels does not exist and thus was not removed.
     * @deprecated This method is deprecated and thus not supported anymore. Help commands are now removed like any other command,
     * using CommandSettings#remove.
     */
    @Deprecated
    public boolean removeHelpLabels(@Nonnull Collection<String> labels) {
        return this.helpLabels.removeAll(labelIgnoreCase ? labels.stream().map(String::toLowerCase).collect(Collectors.toList()) : labels);
    }

    /**
     * This can be used to deactivate the help labels. Removes every help label.
     * @return The current object. This is to use fluent interface.
     * @deprecated This method is deprecated and thus not supported anymore. Help commands are now removed like any other command,
     * using CommandSettings#remove.
     */
    @Deprecated
    public CommandSettings clearHelpLabels() {
        this.helpLabels.clear();
        return this;
    }

    /**
     * Adds a given channel to the blacklist (meaning commands can not be executed in there).
     * @param channelId the id of the channel to be blacklisted.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings addChannelToBlacklist(long channelId) {
        this.blacklistedChannels.add(channelId);
        return this;
    }

    /**
     * Adds multiple channels to the blacklist.
     * @param channelIds multiple ids or an array of ids to be added.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings addChannelsToBlacklist(long... channelIds) {
        for (long id : channelIds)
            this.addChannelToBlacklist(id);
        return this;
    }

    /**
     * Adds multiple channels to the blacklist.
     * @param channelIds A Collection of channel ids to be added.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings addChannelsToBlacklist(Collection<Long> channelIds) {
        this.blacklistedChannels.addAll(channelIds);
        return this;
    }

    /**
     * Removes one channel from the blacklist.
     * @param channelId the id of the channel to remove.
     * @return true, if this was successful, false, if not.
     */
    public boolean removeChannelFromBlacklist(long channelId) {
        return this.blacklistedChannels.remove(channelId);
    }

    /**
     * Removes one or more channels from the blacklist.
     * @param channelIds The ids of the channels to remove.
     * @return true, if this was successful, false, if not.
     */
    public boolean removeChannelsFromBlacklist(long... channelIds) {
        boolean success = true;
        for (long id : channelIds) {
            if (!this.removeChannelFromBlacklist(id)) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Removes a given Collection of channel ids from the blacklist.
     * @param channelIds the Collection to remove.
     * @return true, if this was successful, false, if not.
     */
    public boolean removeChannelsFromBlackList(Collection<Long> channelIds) {
        return this.blacklistedChannels.removeAll(channelIds);
    }

    /**
     * Clears the blacklist so that no channel is blacklisted anymore.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings clearBlacklist() {
        this.blacklistedChannels.clear();
        return this;
    }

    /**
     * Use this method to add commands from your project. Every command which is supposed to be active should be added by this.
     * <p>
     * The two parameters will be put in a HashMap which is used by the API to notice commands.
     * @param label The label which describes your command, i.e. the string after the prefix [prefix][label].
     * @param executor An instance of your command class which implements ICommand.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException If the label is empty or consists of multiple words.
     */
    public CommandSettings put(@Nonnull ICommand executor, String label) {
        if (label.matches(VALID_LABEL))
            this.commands.put(labelIgnoreCase ? label.toLowerCase() : label, executor);
        else
            throw new CommandSetException(INVALID_LABEL_MESSAGE, new IllegalArgumentException("Label " + label + " is not valid"));

        return this;
    }

    /**
     * Use this method to add commands with aliases. <p>
     * Works like put(ICommand, String) but adds multiple labels to the same command.
     * @param executor An instance of your command class which implements ICommand.
     * @param labels One or more labels. This will throw a CommandSetException, if the label is empty or contains spaces.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException If one label is empty or consists of multiple words
     */
    public CommandSettings put(@Nonnull ICommand executor, @Nonnull String... labels) {
        for (String label : labels)
            this.put(executor, label);
        return this;
    }

    /**
     * Use this method to add commands with aliases from a Set. This is not much different from the put-method with Varargs.
     * @param executor An instance of your command class which implements ICommand.
     * @param labels One or more labels. This will throw a CommandSetException, if the label is empty or contains spaces.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException if one label is empty or contains spaces.
     */
    public CommandSettings put(@Nonnull ICommand executor, @Nonnull Collection<String> labels) {
        this.put(executor, labels.toArray(new String[0]));
        return this;
    }

    /**
     * Use this method to remove existing commands. <p>
     * @param label The label of the command to remove.
     * @return true, if the label was successfully removed. false, if the given label doesn't exist.
     */
    public boolean remove(String label) {
        return this.commands.remove(labelIgnoreCase ? label.toLowerCase() : label) != null;
    }

    /**
     * Use this method to remove more than one command at a time. <p>
     * @param labels One or more labels to remove
     * @return true, if every label was successfully removed. false, if one of the given labels does not exist and thus was not removed.
     */
    public boolean remove(@Nonnull String... labels) {
        boolean success = true;
        for (String label : labels) {
            if (!this.remove(label))
                success = false;
        }
        return success;
    }

    /**
     * Removes every label that is in the given Collection.
     * @param labels The labels to remove.
     * @return true, if every label was successfully removed. False, if not (e.g. the label didn't exist)
     */
    public boolean remove(@Nonnull Collection<String> labels) {
        return this.remove(labels.toArray(new String[0]));
    }

    /**
     * Clears all commands.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings clearCommands() {
        this.commands.clear();
        return this;
    }

    /**
     * Resets this whole instance by clearing the commands and setting everything to how it was at the beginning.
     * This instance will also be deactivated if it is not already.<br>
     * Values that were assigned in the constructor will keep their current value.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings clear() {
        this.clearBlacklist().clearCommands().clearCustomPrefixes();
        this.botExecution = false;
        this.cooldown = 0;
        this.helpColor = null;
        this.resetCooldown = false;
        if (this.activated)
            this.deactivate();
        return this;
    }

    /**
     * Use this method to set the default prefix.
     * @param prefix The prefix to set. In case the given String is empty, this will throw a CommandSetException.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException if a non-null prefix does not match the requirements for a valid prefix.
     */
    public CommandSettings setDefaultPrefix(@Nonnull String prefix) {
        if (prefix.matches(VALID_PREFIX))
            this.defaultPrefix = prefix;
        else
            throw new CommandSetException(INVALID_PREFIX_MESSAGE, new IllegalArgumentException("Prefix " + prefix + " is not valid"));
        return this;
    }

    /**
     * Sets a Message that will be sent in the event of a Member using the prefix without executing any valid command. By default, it is null. If it is null, no Message will be sent.
     * @param message Nullable Message object that will be wrapped in a new MessageBuilder to prevent the usage of already sent Messages. If this is null, the message is deactivated.
     * @return The current object. This is to use fluent interface.
     * @see MessageBuilder
     */
    public CommandSettings setUnknownCommandMessage(@Nullable Message message) {
        if (message == null)
            this.unknownCommandMessage = null;
        else
            this.unknownCommandMessage = new MessageBuilder(message).build();
        return this;
    }

    /**
     * Use this method to add a custom command prefix to a guild. This will only work if you instantiated this class with useCustomPrefixes set to true.
     * You can remove the custom prefix from a guild by setting its prefix to null.
     * @param guildId The guild id as a long.
     * @param prefix The nullable prefix to be set.
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException if a non-null prefix does not match the requirements for a valid prefix.
     */
    public CommandSettings setCustomPrefix(long guildId, @Nullable String prefix) {
        if (prefix != null && !prefix.matches(VALID_PREFIX))
            throw new CommandSetException(INVALID_PREFIX_MESSAGE, new IllegalArgumentException("Prefix " + prefix + " is not valid"));
        this.prefixMap.put(guildId, prefix);
        return this;
    }

    /**
     * You may use this method as another way to add custom prefixes. This might be useful if you have many guilds to set
     * prefixes for, because this bulk adds the Map parameter.
     * @param guildIdPrefixMap A Map which contains the prefix for each guild to add. Key: guild ID (Long), Value: prefix (String)
     * @return The current object. This is to use fluent interface.
     * @throws CommandSetException if one of the prefixes is not valid.
     */
    public CommandSettings setCustomPrefixes(@Nonnull Map<Long, String> guildIdPrefixMap) {
        if (guildIdPrefixMap.values().stream().allMatch((prefix) -> prefix.matches(VALID_PREFIX)))
            prefixMap.putAll(guildIdPrefixMap);
        else
            throw new CommandSetException("One or more of the prefixes is not valid: " + INVALID_PREFIX_MESSAGE, new IllegalArgumentException("Invalid prefix"));
        return this;
    }

    /**
     * Removes all entries from the prefix map, resetting every custom prefix.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings clearCustomPrefixes() {
        this.prefixMap.clear();
        return this;
    }

    /**
     * Sets the cooldown for this instance of settings. If someone executes a command before the cooldown has expired, it won't be called.
     * @param msCooldown the cooldown in milliseconds.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings setCooldown(long msCooldown) {
        this.cooldown = msCooldown;
        return this;
    }

    /**
     * Sets the parameter resetCooldown. Should be used in combination with the cooldown function of this API.
     * By default, this is false.
     * @param resetCooldown True: The command cooldown is reset on each attempt to execute a command. E.g.:
     *                      A User executes an command and gets a 10 second cooldown. If he tries to execute another command within these 10 seconds,
     *                      the command isn't executed and the cooldown is at 10 seconds again.<p>
     *                      False: Once the cooldown is activated, it will not be reset by further attempts to execute commands.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings setResetCooldown(boolean resetCooldown) {
        this.resetCooldown = resetCooldown;
        return this;
    }

    /**
     * Setter for the field botExecution. Decides whether bots may execute commands. By default, this is NOT the case.
     * @param botExecution true, if you want to allow bots to execute commands. false, if not.
     * @return The current object. This is to use fluent interface.
     */
    public CommandSettings setBotExecution(boolean botExecution) {
        this.botExecution = botExecution;
        return this;
    }

    /**
     * Sets the color the help message embed will have if you use DefaultHelpCommand.
     * By default, it will always be the color of the self member.
     * @param color The color to set. To set this to self member color, set it to null.
     * @return The current object. This is to use fluent interface.
     * @see DefaultHelpCommand
     */
    public CommandSettings setHelpCommandColor(@Nullable Color color) {
        this.helpColor = color;
        return this;
    }

    /**
     * Sets the prefix and the command HashMap for the rest of the API. This is the last method to call when having finished setting up your commands.<p>
     * Note that activating multiple CommandSettings may cause problems. You can do this to use multiple prefixes, but it is not recommended.<p>
     * This method is important to call because otherwise no command will be registered by the internal command listener.
     * @throws CommandSetException if you already activated this instance.
     */
    public void activate() {
        if (!this.activated) {
            if (useShardManager)
                ((ShardManager)jda).addEventListener(listener);
            else
                ((JDA)jda).addEventListener(listener);
            this.activated = true;
            LOGGER.info("CommandSettings were activated");
        } else
            throw new CommandSetException("CommandSettings already activated!", new IllegalStateException("Cannot activate CommandSettings in current state"));
    }

    /**
     * Deactivates the current CommandSettings by removing the command listener from jda.
     * The CommandSettings can be activated again by using activate().
     * @throws CommandSetException if you either did not activate this instance or already deactivated it.
     */
    public void deactivate() {
        if (this.activated) {
            if (useShardManager)
                ((ShardManager)jda).removeEventListener(listener);
            else
                ((JDA)jda).removeEventListener(listener);
            this.activated = false;
            LOGGER.info("CommandSettings were deactivated");
        } else
            throw new CommandSetException("CommandSettings weren't activated yet and can therefore not be deactivated!", new IllegalStateException("Cannot deactivate CommandSettings in current state"));
    }

    /**
     * Use this method to get the prefix for a specific guild.
     * @param guildId The id of the guild to check.
     * @return the default prefix, if there is no custom prefix set for the given guild id. Otherwise, it returns the custom prefix.
     */
    public String getPrefix(long guildId) {
        String prefix = this.prefixMap.get(guildId);
        return prefix != null ? prefix : defaultPrefix;
    }

    /**
     * Use this method to get the default prefix.
     * @return default prefix set in the constructor or the method setDefaultPrefix(String).
     */
    public String getPrefix() {
        return this.defaultPrefix;
    }

    /**
     * Use this to get the blacklisted channels.
     * @return an unmodifiable Set with all blacklisted channels.
     */
    public Set<Long> getBlacklistedChannels() {
        return Collections.unmodifiableSet(this.blacklistedChannels);
    }

    /**
     * Returns whether the given channel id is blacklisted.
     * @param channelId The id of the channel to check for.
     * @return true, if the blacklist Set contains this id. False, if not.
     */
    public boolean isBlacklisted(long channelId) {
        return this.blacklistedChannels.contains(channelId);
    }

    /**
     * Returns every registered label in a Set. Note that in case you activated isLabelIgnoreCase, every label in there will be in lower case.
     * Adding or removing something will not have any effect. This can primarily be used to iterate over the labels.
     * @return an unmodifiable Set of labels.
     */
    public Set<String> getLabelSet() {
        return Collections.unmodifiableSet(this.commands.keySet());
    }

    /**
     * Returns every registered label for an ICommand instance in an immutable Set. Note that in case you activated isLabelIgnoreCase, every label in there will be in lower case.
     * Adding or removing something will not have any effect. This can primarily be used to get a command's aliases.
     * @param command The {@link com.github.johnnyjayjay.discord.commandapi.ICommand ICommand} instance to get the labels from
     * @return an unmodifiable Set of labels.
     */
    public Set<String> getLabels(ICommand command) {
        return Collections.unmodifiableSet(this.commands.keySet().stream().filter((label) -> this.commands.get(label).equals(command)).collect(Collectors.toSet()));
    }
    
    /**
     * Returns all of the registered help labels.
     * @return an unmodifiable Set of Strings that are registered as help labels.
     * @deprecated This method is deprecated and not supported in this version anymore. It will be removed in a future release.
     * Help labels can now be retrieved with CommandSettings#getLabels(ICommand).
     */
    @Deprecated
    public Set<String> getHelpLabelSet() {
        return Collections.unmodifiableSet(this.helpLabels);
    }

    /**
     * Returns whether this instance is activated or not.
     * @return true, if it is, false, if not.
     */
    public boolean isActivated() {
        return this.activated;
    }

    /**
     * Returns the help color for DefaultHelpCommand.
     * @return possibly-null java.awt.Color.
     */
    public Color getHelpColor() {
        return this.helpColor;
    }

    /**
     * Returns the currently set cooldown for this instance.
     * @return The cooldown in milliseconds.
     */
    public long getCooldown() {
        return this.cooldown;
    }

    /**
     * Returns whether command labels are case insensitive on this instance of CommandSettings.
     * @return true, if ignore case for labels is activated. False, if not.
     */
    public boolean isLabelIgnoreCase() {
        return this.labelIgnoreCase;
    }

    /**
     * Returns whether the cooldown will be reset for each execution attempt. See the setter of this for more.
     * @return True, if the cooldown is reset each time. False, if not.
     */
    public boolean isResetCooldown() {
        return resetCooldown;
    }

    /**
     * Returns whether bot execution is enabled.
     * @return True, if bots may execute commands. False, if not.
     */
    public boolean botsMayExecute() {
        return this.botExecution;
    }

    /**
     * Transforms this instance to a String, showing the current set options.
     * @return This instance as a String.
     */
    @Override
    public String toString() {
        return "CommandSettings{" +
                "unknownCommandMessage=" + unknownCommandMessage +
                ", defaultPrefix='" + defaultPrefix + '\'' +
                ", cooldown=" + cooldown +
                ", helpColor=" + helpColor +
                ", blacklistedChannels=" + blacklistedChannels +
                ", prefixMap=" + prefixMap +
                ", commands=" + commands +
                ", activated=" + activated +
                ", labelIgnoreCase=" + labelIgnoreCase +
                ", resetCooldown=" + resetCooldown +
                ", botExecution=" + botExecution +
                '}';
    }

    protected Message getUnknownCommandMessage() {
        return unknownCommandMessage;
    }

    protected Map<String, ICommand> getCommands() {
        return this.commands;
    }
}
