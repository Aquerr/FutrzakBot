package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class FutrzakAudioPlayer extends AudioEventAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakAudioPlayer.class);

    private final long guildId;
    private final AudioPlayer audioPlayer;
    private final Queue<AudioTrack> tracksQueue = new ArrayDeque<>();
    private Instant lastTrackEndTime = Instant.now();

    public FutrzakAudioPlayer(long guildId, AudioPlayer audioPlayer)
    {
        this.guildId = guildId;
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
    }

    public void queue(AudioTrack track)
    {
        this.tracksQueue.offer(track);

        if (this.audioPlayer.getPlayingTrack() == null)
        {
            playNextTrack();
        }
    }

    public void playNextTrack()
    {
        AudioTrack audioTrack = this.tracksQueue.poll();
        if(audioTrack != null)
        {
            LOGGER.info("Starting playing: {}", audioTrack.getInfo().title);
        }
        this.audioPlayer.playTrack(audioTrack);
    }

    @Override
    public void onPlayerPause(AudioPlayer player)
    {
        LOGGER.info("Player paused!");
    }

    @Override
    public void onPlayerResume(AudioPlayer player)
    {
        LOGGER.info("Player resumed!");

    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        LOGGER.info("Track started!");

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        LOGGER.info("Track ended!");
        if (endReason.mayStartNext)
        {
            playNextTrack();
        }

        if (this.audioPlayer.getPlayingTrack() != null)
        {
            this.lastTrackEndTime = Instant.now();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        LOGGER.error("Track exception: " + exception.getMessage());
        this.lastTrackEndTime = Instant.now();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs)
    {
        LOGGER.info("Track stuck!");
        this.lastTrackEndTime = Instant.now();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace)
    {
        LOGGER.info("Track stuck!");
        this.lastTrackEndTime = Instant.now();
    }

    public long getGuildId()
    {
        return guildId;
    }

    public AudioTrack getPlayingTrack()
    {
        return this.audioPlayer.getPlayingTrack();
    }

    public void stopPlayer(MessageChannel textChannel)
    {
        this.audioPlayer.stopTrack();
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createPlayerStoppedMessage()).queue();
    }

    public void resumePlayer(TextChannel textChannel)
    {
        this.audioPlayer.setPaused(false);
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createPlayerResumedMessage()).queue();
    }

    public void setVolume(int volume, TextChannel textChannel)
    {
        this.audioPlayer.setVolume(volume);
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createPlayerVolumeChangedMessage(volume)).queue();
    }

    public AudioPlayer getInternalAudioPlayer()
    {
        return audioPlayer;
    }

    public Instant getLastTrackEndTime()
    {
        return lastTrackEndTime;
    }

    public List<AudioTrack> getQueue()
    {
        return List.copyOf(this.tracksQueue);
    }
}
