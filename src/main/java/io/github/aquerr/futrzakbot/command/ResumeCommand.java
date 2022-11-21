package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;

public class ResumeCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public ResumeCommand(final FutrzakAudioPlayerManager futrzakAudioPlayerManager, final MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        resumePlayer(context.getTextChannel());
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("resume");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.resume.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.resume.description");
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        event.reply(messageSource.getMessage("command.resume.resuming")).complete();
        resumePlayer(event.getTextChannel());
    }

    private void resumePlayer(TextChannel textChannel)
    {
        this.futrzakAudioPlayerManager.resume(textChannel.getGuild().getIdLong(), textChannel);
    }
}
