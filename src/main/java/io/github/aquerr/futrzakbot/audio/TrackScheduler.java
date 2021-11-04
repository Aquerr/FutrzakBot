package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer audioPlayer;
    private final Queue<AudioTrack> tracksQueue = new ArrayDeque<>();

    public TrackScheduler(AudioPlayer audioPlayer)
    {
        this.audioPlayer = audioPlayer;
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
        this.audioPlayer.playTrack(audioTrack);
        this.audioPlayer.setVolume(100);
        System.out.println("Starting playing: " + audioTrack.getInfo().title);
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
            AudioTrack nextTrack = this.tracksQueue.poll();
            if (nextTrack != null)
            {
                this.audioPlayer.playTrack(nextTrack);
            }
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        LOGGER.info("Track exception: " + exception.getMessage());

    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs)
    {
        LOGGER.info("Track stuck!");

    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace)
    {
        LOGGER.info("Track stuck!");
    }

    public void playTrack(AudioTrack track)
    {

    }
}
