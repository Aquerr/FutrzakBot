package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public final class FutrzakAudioPlayerManager
{
    public static FutrzakAudioPlayerManager getInstance()
    {
        return FutrzakAudioPlayerManagerInstanceHolder.INSTANCE;
    }

    private static class FutrzakAudioPlayerManagerInstanceHolder
    {
        private static final FutrzakAudioPlayerManager INSTANCE = new FutrzakAudioPlayerManager();
    }

    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, FutrzakAudioPlayer> guildAudioPlayers = new HashMap<>();

    private FutrzakAudioPlayerManager()
    {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public void queue(long guildId, TextChannel textChannel, String trackName)
    {

        this.audioPlayerManager.loadItem("ytsearch: " + trackName, new FutrzakAudioLoadHandler(guildId, getOrCreateAudioPlayer(guildId), textChannel));
    }

    public void playNextTrack(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.playNextTrack();
        AudioTrack track = futrzakAudioPlayer.getPlayingTrack();
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createNowPlayingMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    public void stop(long guildId, TextChannel textChannel)
    {
        getOrCreateAudioPlayer(guildId).stopPlayer(textChannel);
    }

    public void resume(long guildId, TextChannel textChannel)
    {
        getOrCreateAudioPlayer(guildId).resumePlayer(textChannel);
    }

    public void setVolume(long guildId, TextChannel textChannel, int volume)
    {
        getOrCreateAudioPlayer(guildId).setVolume(volume, textChannel);
    }

    public FutrzakAudioPlayer getOrCreateAudioPlayer(long guildId)
    {
        return this.guildAudioPlayers.computeIfAbsent(guildId, id -> new FutrzakAudioPlayer(id, this.audioPlayerManager.createPlayer()));
    }
}
