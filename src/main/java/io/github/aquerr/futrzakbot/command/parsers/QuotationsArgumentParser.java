package io.github.aquerr.futrzakbot.command.parsers;

public class QuotationsArgumentParser
{
    public static String parse(StringBuilder input)
    {
        if(input.charAt(0) != '\"')
            return null;

        input.deleteCharAt(0);

        int closingQuoteIndex = input.indexOf("\"");
        if(closingQuoteIndex == -1)
            return null;

        String parsedArgument = input.substring(0, closingQuoteIndex);
        input.delete(0, closingQuoteIndex + 1);

        return parsedArgument;
    }
}
