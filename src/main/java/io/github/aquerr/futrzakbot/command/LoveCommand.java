package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import io.github.aquerr.futrzakbot.games.LoveMeter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand(argsCount = 1)
public class LoveCommand implements Command
{
    @Override
    public boolean execute(Member member, TextChannel channel, List<String> args)
    {
        Message loveMessage = LoveMeter.checkLove(member, args.get(0));
        channel.sendMessage(loveMessage).queue();
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f love <użytkownik>";
    }

    @Override
    public String getHelpName()
    {
        return ":heart: Licznik miłości: ";
    }
}
