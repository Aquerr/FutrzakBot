package io.github.aquerr.futrzakbot.command.parsing.parsers;

import io.github.aquerr.futrzakbot.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.command.parsing.ParsingContext;

public interface ArgumentParser<T>
{
    T parse(ParsingContext input) throws ArgumentParseException;
}
