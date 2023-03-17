package io.github.aquerr.futrzakbot.discord.command.parsing.parsers;

import io.github.aquerr.futrzakbot.discord.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.discord.command.parsing.ParsingContext;

/**
 * Represents an argument parser that can convert the given parsing context to a value.
 * @param <T> the type of parsed value.
 */
public interface ArgumentParser<T>
{
    /**
     * Parses the given parsing context to desired value.
     * @param context the parsing context
     * @return the parsed value
     * @throws ArgumentParseException if argument could not be parsed to required value.
     */
    T parse(ParsingContext context) throws ArgumentParseException;
}
