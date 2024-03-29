package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

public class StopCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public StopCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        stopMusicPlayer(context.getGuildMessageChannel());
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("stop");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.stop.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.stop.description");
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        event.reply(messageSource.getMessage("command.stop.stopping-player")).complete();
        stopMusicPlayer(event.getChannel().asGuildMessageChannel());
    }

    private void stopMusicPlayer(GuildMessageChannel channel)
    {
        this.futrzakAudioPlayerManager.stop(channel.getGuild().getIdLong(), channel);
    }
}
