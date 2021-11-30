package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.audio.handler.FutrzakAudioLoadHandler;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class FutrzakAudioPlayerManager
{
    private final FutrzakBot futrzakBot;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, FutrzakAudioPlayer> guildAudioPlayers = new ConcurrentHashMap<>();

    private final ScheduledFuture<?> botKickSechuldedFuture = Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(this::botKickTaskRun, 5, 5, TimeUnit.MINUTES);

    public FutrzakAudioPlayerManager(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public void queue(long guildId, TextChannel textChannel, String trackName)
    {
        this.audioPlayerManager.loadItem("ytsearch: " + trackName, new FutrzakAudioLoadHandler(guildId, getOrCreateAudioPlayer(guildId), textChannel));
    }

    public void skipAndPlayNextTrack(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        AudioTrack audioTrack = futrzakAudioPlayer.getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSkipTrackMessage(audioTrack)).queue();
        }

        futrzakAudioPlayer.playNextTrack();
        audioTrack = futrzakAudioPlayer.getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createNowPlayingMessage(audioTrack)).queue();
        }
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

    private void botKickTaskRun()
    {
        final Iterator<Map.Entry<Long, FutrzakAudioPlayer>> futrzakAudioPlayersIterator = this.guildAudioPlayers.entrySet().iterator();
        while (futrzakAudioPlayersIterator.hasNext())
        {
            Map.Entry<Long, FutrzakAudioPlayer> futrzakAudioPlayerEntry = futrzakAudioPlayersIterator.next();
            FutrzakAudioPlayer futrzakAudioPlayer = futrzakAudioPlayerEntry.getValue();
            if (futrzakAudioPlayer.getPlayingTrack() == null && futrzakAudioPlayer.getLastTrackEndTime().plus(5, ChronoUnit.MINUTES).isBefore(Instant.now()))
            {
                // Kick bot
                this.futrzakBot.getJda().getGuildById(futrzakAudioPlayerEntry.getKey()).getAudioManager().closeAudioConnection();
                // Delete player
                this.guildAudioPlayers.remove(futrzakAudioPlayer.getGuildId());
            }
        }
    }

    public List<AudioTrack> getQueue(long guildId)
    {
        return this.guildAudioPlayers.get(guildId).getQueue();
    }
}
