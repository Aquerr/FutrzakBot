package io.github.aquerr.futrzakbot.discord.command.parsing;

import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.ArgumentParser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

/**
 * Represents a context in which an argument is being parsed.
 * Used in {@link ArgumentParser}.
 */
public interface ParsingContext
{
    /**
     * The messsage channel in which parsing occurs
     * @return the associated text channel.
     */
    MessageChannelUnion getMessageChannel();

    /**
     * The argument that is being parsed.
     * @return the argument as {@link String}.
     */
    String getArgument();
}
