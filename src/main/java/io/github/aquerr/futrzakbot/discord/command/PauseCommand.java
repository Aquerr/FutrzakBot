package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

public class PauseCommand implements Command, SlashCommand
{

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public PauseCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        pauseMusicPlayer(context.getTextChannel());
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("pause");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.pause.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.pause.description");
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        event.reply(messageSource.getMessage("command.pause.pausing-player")).complete();
        pauseMusicPlayer(event.getChannel().asTextChannel());
    }

    private void pauseMusicPlayer(TextChannel textChannel)
    {
        this.futrzakAudioPlayerManager.pause(textChannel.getGuild().getIdLong(), textChannel);
    }
}
