package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;

public class SkipCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    private final MessageSource messageSource;

    public SkipCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.skipAndPlayNextTrack(textChannel.getGuild().getIdLong(), textChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("skip");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.skip.name");
    }

    @Override
    public String getDescription()
    {
        return this.messageSource.getMessage("command.skip.description");
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        event.reply(this.messageSource.getMessage("command.skip.skipping")).queue();
        TextChannel textChannel = event.getTextChannel();
        this.futrzakAudioPlayerManager.skipAndPlayNextTrack(textChannel.getGuild().getIdLong(), textChannel);
    }
}
