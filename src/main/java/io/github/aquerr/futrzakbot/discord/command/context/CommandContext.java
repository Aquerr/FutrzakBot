package io.github.aquerr.futrzakbot.discord.command.context;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

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
     * @return the {@link TextChannel}
     */
    TextChannel getTextChannel();

    /**
     * Gets guild member that is the invoker of the command.
     * @return the {@link Member}
     */
    Member getMember();

}
