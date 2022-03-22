package io.github.aquerr.futrzakbot.command.parsing.parsers;

import io.github.aquerr.futrzakbot.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.command.parsing.ParsingContext;

public class StringArgumentParser implements ArgumentParser<String>
{
    public String parse(ParsingContext context) throws ArgumentParseException
    {
        String argument = context.getArgument();
        String trimmedString = argument.trim();
        if (argument.contains(" "))
            throw new ArgumentParseException("Input should be one word but space has been detected. Input '" + argument + "'");
        return trimmedString;
    }
}
