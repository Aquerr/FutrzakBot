package io.github.aquerr.futrzakbot.command.futrzak;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.games.FutrzakGame;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FutrzakCommand implements Command
{
    private final FutrzakGame futrzakGame;

    public FutrzakCommand(FutrzakGame futrzakGame)
    {
        this.futrzakGame = futrzakGame;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel channel = context.getTextChannel();
        Member member = context.getMember();
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
    public List<String> getAliases()
    {
        return Collections.singletonList("futrzak");
    }

    @Override
    public String getName()
    {
        return ":tiger: Sprawdź status swojego futrzaka: ";
    }

    @Override
    public String getDescription()
    {
        return "Sprawdź status swojego futrzaka";
    }

    @Override
    public List<Command> getSubCommands()
    {
        return List.of(new FightCommand());
    }
}
