package io.github.aquerr.futrzakbot.command.parsers;

public class QuotationsArgumentParser implements ArgumentParser<String>
{
    public String parse(String input)
    {
        if(input.charAt(0) != '\"')
            return null;

        input = input.substring(1);

        int closingQuoteIndex = input.indexOf("\"");
        if(closingQuoteIndex == -1)
            return null;

        return input.substring(0, closingQuoteIndex);
    }
}
