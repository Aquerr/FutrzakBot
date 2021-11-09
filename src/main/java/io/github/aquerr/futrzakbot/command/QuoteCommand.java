package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import io.github.aquerr.futrzakbot.games.QuoteGame;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand
public class QuoteCommand implements Command
{
    private final QuoteGame quoteGame;

    public QuoteCommand(QuoteGame quoteGame)
    {
        this.quoteGame = quoteGame;
    }

    @Override
    public boolean execute(Member member, TextChannel channel, List<String> args)
    {
        this.quoteGame.printQuote(channel);
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f cytat";
    }

    @Override
    public String getHelpName()
    {
        return ":thought_balloon: Cytat: ";
    }
}
