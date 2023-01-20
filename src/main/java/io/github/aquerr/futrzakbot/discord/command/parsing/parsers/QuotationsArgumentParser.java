package io.github.aquerr.futrzakbot.discord.command.parsing.parsers;

import io.github.aquerr.futrzakbot.discord.command.parsing.ParsingContext;

public class QuotationsArgumentParser implements ArgumentParser<String>
{
    public String parse(ParsingContext parsingContext)
    {
        String argument = parsingContext.getArgument();
        if(argument.charAt(0) != '\"')
            return null;

        argument = argument.substring(1);

        int closingQuoteIndex = argument.indexOf("\"");
        if(closingQuoteIndex == -1)
            return null;

        return argument.substring(0, closingQuoteIndex);
    }
}
