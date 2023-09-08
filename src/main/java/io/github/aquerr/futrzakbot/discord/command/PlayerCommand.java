package io.github.aquerr.futrzakbot.discord.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;

public class PlayerCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;
    private final MessageSource messageSource;

    public PlayerCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager,
                         FutrzakMessageEmbedFactory messageEmbedFactory,
                         MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageEmbedFactory = messageEmbedFactory;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(textChannel.getGuild().getIdLong()).getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(messageEmbedFactory.createNowPlayingMessage(audioTrack)).queue();
        }
        else
        {
            textChannel.sendMessageEmbeds(messageEmbedFactory.createNothingIsPlayingMessage()).queue();
        }
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("player");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.player.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.player.description");
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        //TODO: Show player buttons...
        return SlashCommand.super.getSlashCommandData();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        TextChannel textChannel = event.getChannel().asTextChannel();
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(textChannel.getGuild().getIdLong()).getPlayingTrack();
        if (audioTrack != null)
        {
            event.replyEmbeds(this.messageEmbedFactory.createNowPlayingMessage(audioTrack)).queue();
        }
        else
        {
            event.replyEmbeds(this.messageEmbedFactory.createNothingIsPlayingMessage()).queue();
        }
    }
}
