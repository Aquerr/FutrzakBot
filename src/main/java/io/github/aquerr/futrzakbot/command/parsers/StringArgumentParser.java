package io.github.aquerr.futrzakbot.command.parsers;

public class StringArgumentParser implements ArgumentParser<String>
{
    public String parse(String input)
    {
        String trimmedString = input.trim();
        if (input.contains(" "))
            throw new IllegalArgumentException("Input should be one word but space has been detected. Input '" + input + "'");
        return trimmedString;
    }
}
