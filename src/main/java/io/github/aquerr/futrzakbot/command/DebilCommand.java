package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand(argsCount = 1)
public class DebilCommand implements Command
{

    @Override
    public boolean execute(Member member, TextChannel channel, List<String> args)
    {
        channel.sendMessage("To Ty ").append(member.getAsMention()).append(" :clown:").complete();
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f debil <user>";
    }

    @Override
    public String getHelpName()
    {
        return ":japanese_goblin: debil?";
    }
}
