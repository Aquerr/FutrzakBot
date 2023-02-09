package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;

public class ClearCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    public ClearCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
        this.messageEmbedFactory = messageEmbedFactory;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.clearQueue(textChannel.getGuild().getIdLong(),textChannel);
        textChannel.sendMessage(messageEmbedFactory.createClearMessage()).queue();
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
        event.replyEmbeds(messageEmbedFactory.createClearMessage()).queue();
    }
}
