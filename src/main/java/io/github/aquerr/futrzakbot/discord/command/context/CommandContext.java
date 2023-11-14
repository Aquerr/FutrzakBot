package io.github.aquerr.futrzakbot.discord.command.context;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.Optional;

public interface CommandContext
{
    /**
     * Retrieves safely value of the parameter for the given key
     * @param key the key of the parameter that the value should be retrieved
     * @return the value packed in an Optional, or {@link Optional#empty()} if value for the given key does not exist
     */
    <T> Optional<T> get(String key);

    /**
     * Force-retrieves value of the parameter for the given key.
     * @param key the key of the parameter that the value should be retrieved
     * @return the value or throws IllegalStateException if value does not exist
     */
    <T> T require(String key);

    /**
     * Gets the channel that the command has been invoked in.
     * @return the {@link GuildMessageChannel} or null if command was not invoked in guild channel.
     */
    GuildMessageChannel getGuildMessageChannel();

    /**
     * Gets the private channel that the command has been invoked in.
     * @return the {@link PrivateChannel} or null if command was not invoked in private channel.
     */
    PrivateChannel getPrivateChannel();

    /**
     * Gets the channel that the command has been invoked in.
     * @return the {@link MessageChannelUnion}.
     */
    MessageChannelUnion getMessageChannel();

    /**
     * Gets guild member that is the invoker of the command.
     * @return the {@link Member}
     */
    Member getMember();

}
