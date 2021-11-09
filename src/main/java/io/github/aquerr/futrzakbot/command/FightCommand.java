package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand
public class FightCommand implements Command
{
    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        textChannel.sendMessage("Ta funkcja nie została jeszcze w pełni dodana :/").complete();
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f fight <użytkownik>";
    }

    @Override
    public String getHelpName()
    {
        return ":crossed_swords: Walcz z futrzakiem innej osoby: ";
    }
}
