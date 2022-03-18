package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.games.QuoteGame;

import java.util.Arrays;
import java.util.List;

public class QuoteCommand implements Command
{
    private final QuoteGame quoteGame;

    public QuoteCommand(QuoteGame quoteGame)
    {
        this.quoteGame = quoteGame;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        this.quoteGame.printRandomQuote(context.getTextChannel(), context.getMember());
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("quote", "cytat");
    }

    @Override
    public String getName()
    {
        return ":thought_balloon: Cytat: ";
    }

    @Override
    public String getDescription()
    {
        return "Wylosuj cytat";
    }
}
