package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.games.RouletteGame;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class RouletteCommand implements Command
{
    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel channel = context.getTextChannel();
        Member member = context.getMember();
        long guildId = channel.getGuild().getIdLong();
        if (!RouletteGame.isActive(guildId))
        {
            channel.sendMessage(member.getAsMention()).append(" rozpoczyna nową grę w ruletkę!").complete();
            channel.sendMessage("Będzie gorąco!").complete();
            RouletteGame.startNewGame(guildId);
        }

        boolean killed = RouletteGame.usePistol(guildId);

        if (killed)
        {
            channel.sendMessage(member.getAsMention()).append(" pociąga za spust!").complete();
            channel.sendMessage("STRZAŁ!").complete();
            channel.sendMessage(member.getAsMention()).append(" jest już w innym świecie :') ").complete();
            channel.getGuild().mute(member, true).reason("Mutuję Cię na 30sekund!").complete();
        }
        else
        {
            channel.sendMessage(member.getAsMention()).append(" pociąga za spust!").complete();
            channel.sendMessage("Z pistoletu słychać tylko odgłos kliknięcia!").complete();
            channel.sendMessage(member.getAsMention()).append(" udało się przeżyć ruletkę.").complete();
        }
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("ruletka");
    }

    @Override
    public String getName()
    {
        return ":boom: Rosyjska ruletka: ";
    }

    @Override
    public String getDescription()
    {
        return "Rosyjska ruletka";
    }
}
