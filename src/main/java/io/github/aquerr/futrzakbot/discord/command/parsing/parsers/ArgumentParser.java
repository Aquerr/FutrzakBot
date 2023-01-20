package io.github.aquerr.futrzakbot.discord.command.parsing.parsers;

import io.github.aquerr.futrzakbot.discord.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.discord.command.parsing.ParsingContext;

public interface ArgumentParser<T>
{
    T parse(ParsingContext input) throws ArgumentParseException;
}
