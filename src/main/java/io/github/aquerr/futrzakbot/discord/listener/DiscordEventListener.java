package io.github.aquerr.futrzakbot.discord.listener;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class DiscordEventListener implements EventListener
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public DiscordEventListener(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event)
    {
        if (event instanceof GuildVoiceUpdateEvent newEvent)
        {
            onGuildVoiceUpdate(newEvent);
        }
    }

    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event)
    {
        if (isBot(event, event.getMember().getIdLong()) && event.getChannelJoined() != null)
        {
            updateFutrzakPlayerVoiceChannel(event.getChannelJoined().asVoiceChannel());
        }
    }

    private void updateFutrzakPlayerVoiceChannel(VoiceChannel voiceChannel)
    {
        this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(voiceChannel.getIdLong()).connectToVoiceChannel(voiceChannel);
    }

    private boolean isBot(final GenericEvent event, final Long userId)
    {
        return event.getJDA().getSelfUser().getIdLong() == userId;
    }
}
