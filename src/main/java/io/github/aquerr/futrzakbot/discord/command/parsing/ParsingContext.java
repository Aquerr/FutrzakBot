package io.github.aquerr.futrzakbot.discord.command.parsing;

import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.ArgumentParser;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * A class used during parsing of command arguments in classes which implement {@link ArgumentParser}.
 *
 * Represents a context in which an argument is being parsed.
 */
public interface ParsingContext
{
    /**
     * The text channel in which parsing occurs
     * @return the associated text channel.
     */
    TextChannel getTextChannel();

    /**
     * The argument that is being parsed.
     * @return the argument as {@link String}.
     */
    String getArgument();
}
