package io.github.aquerr.futrzakbot.discord.command.parsing.parsers;

import io.github.aquerr.futrzakbot.discord.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.discord.command.parsing.ParsingContext;

public class DoubleArgumentParser implements ArgumentParser<Double>
{
    @Override
    public Double parse(ParsingContext context) throws ArgumentParseException
    {
        String argument = context.getArgument();
        if (argument == null || argument.isBlank())
            throw new ArgumentParseException("Input cannot be empty!");
        return Double.valueOf(argument);
    }
}
