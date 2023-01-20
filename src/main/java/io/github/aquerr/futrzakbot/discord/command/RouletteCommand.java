package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.games.RouletteGame;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RouletteCommand implements Command, SlashCommand
{
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(10);

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel channel = context.getTextChannel();
        Member member = context.getMember();
        useRoulette(channel, member);
        return true;
    }

    private void useRoulette(TextChannel channel, Member member)
    {
        long guildId = channel.getGuild().getIdLong();
        if (!RouletteGame.isActive(guildId))
        {
            channel.sendMessage(member.getAsMention() + " rozpoczyna nową grę w ruletkę!").complete();
            channel.sendMessage("Będzie gorąco!").complete();
            RouletteGame.startNewGame(guildId);
        }

        boolean killed = RouletteGame.usePistol(guildId);

        if (killed)
        {
            channel.sendMessage(member.getAsMention() + " pociąga za spust!").complete();
            channel.sendMessage("STRZAŁ! :boom:").complete();
            channel.sendMessage(member.getAsMention() + " jest już w innym świecie :skull_crossbones: (mute na 30 sek)").complete();
            channel.getGuild().mute(member, true).reason("Mutuję Cię na 30 sekund z powodu śmierci w ruletce! :)").complete();
            SCHEDULER.schedule(() -> {
                channel.getGuild().mute(member, false).reason("Zostałeś wskrzeszony! :)").complete();
            }, 30, TimeUnit.SECONDS);
        }
        else
        {
            channel.sendMessage(member.getAsMention() + " pociąga za spust!").complete();
            channel.sendMessage("Z pistoletu słychać tylko odgłos kliknięcia!").complete();
            channel.sendMessage(member.getAsMention() + " udało się przeżyć ruletkę. :sunglasses: ").complete();
        }
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

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        event.reply("Zagrajmy więc!").queue();
        useRoulette(event.getTextChannel(), event.getMember());
    }
}
