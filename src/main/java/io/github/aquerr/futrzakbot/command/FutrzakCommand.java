package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import io.github.aquerr.futrzakbot.games.FutrzakGame;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.util.List;

@BotCommand
public class FutrzakCommand implements Command
{
    private final FutrzakGame futrzakGame;

    public FutrzakCommand(FutrzakGame futrzakGame)
    {
        this.futrzakGame = futrzakGame;
    }

    @Override
    public boolean execute(Member member, TextChannel channel, List<String> args)
    {
        long guildId = channel.getGuild().getIdLong();
        String userId = member.getId();
        if(this.futrzakGame.checkIfFutrzakExists(guildId, userId))
        {
            channel.sendMessageEmbeds(this.futrzakGame.displayFutrzak(guildId, member)).queue();
        }
        else
        {
            channel.sendMessage("Widzę że nie masz jeszcze swojego futrzaka. Tworzę jednego dla Ciebie! :)").queue();
            try
            {
                this.futrzakGame.createFutrzak(channel.getGuild().getIdLong(), member.getId());
            }
            catch (IOException e)
            {
                channel.sendMessage("Coś poszło nie tak :(").queue();
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f futrzak";
    }

    @Override
    public String getHelpName()
    {
        return ":tiger: Sprawdź status swojego futrzaka: ";
    }
}
