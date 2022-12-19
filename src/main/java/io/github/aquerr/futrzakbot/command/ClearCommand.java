package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;

public class ClearCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public ClearCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.clearQueue(textChannel.getGuild().getIdLong(),textChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("clear");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.clear.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.clear.description");
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        this.futrzakAudioPlayerManager.clearQueue(event.getGuild().getIdLong(), event.getTextChannel());
        event.replyEmbeds(FutrzakMessageEmbedFactory.createClearMessage()).queue();
    }
}
