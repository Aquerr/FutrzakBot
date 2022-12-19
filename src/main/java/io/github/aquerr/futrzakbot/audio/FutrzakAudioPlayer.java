package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.github.aquerr.futrzakbot.audio.handler.FutrzakAudioLoadHandler;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class FutrzakAudioPlayer extends AudioEventAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakAudioPlayer.class);

    private final long guildId;
    private boolean isLoop = false;
    private final FutrzakAudioLoadHandler audioLoadHandler;
    private final AudioPlayer audioPlayer;
    private final LinkedList<AudioTrack> tracksQueue = new LinkedList<>();
    private final FutrzakMessageEmbedFactory messageEmbedFactory;
    private TextChannel lastBotUsageChannel;
    private Instant lastTrackEndTime = Instant.now();

    public FutrzakAudioPlayer(long guildId, AudioPlayer audioPlayer)
    {
        this.guildId = guildId;
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.audioLoadHandler = new FutrzakAudioLoadHandler(this, FutrzakMessageEmbedFactory.getInstance());
        this.messageEmbedFactory = FutrzakMessageEmbedFactory.getInstance();
    }

    public void setLastBotUsageChannel(TextChannel lastBotUsageChannel)
    {
        this.lastBotUsageChannel = lastBotUsageChannel;
    }

    public TextChannel getLastBotUsageChannel()
    {
        return lastBotUsageChannel;
    }

    public void queue(AudioTrack track, boolean shouldStartPlaying)
    {
        if(track == null)
            return;

        this.tracksQueue.offer(track);

        if (shouldStartPlaying)
        {
            if (this.audioPlayer.getPlayingTrack() == null)
            {
                skip();
            }
        }
    }

    public void skip()
    {
        queueLastTrackIfLoop();
        AudioTrack audioTrack = this.tracksQueue.poll();
        if(audioTrack != null)
        {
            LOGGER.info("Starting playing: {}", audioTrack.getInfo().title);
            this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createNowPlayingMessage(audioTrack)).complete();
        }
        this.audioPlayer.playTrack(audioTrack);
    }

    public boolean toggleLoop()
    {
        isLoop = !isLoop;
        return isLoop;
    }

    public void clear()
    {
        this.tracksQueue.clear();
        LOGGER.info("cleared the queue");
    }

    public AudioTrack remove(int element)
    {
        return this.tracksQueue.remove(element-1);
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
            skip();
        }

        if (isLoop)
        {
            queue(track.makeClone(), true);
        }

        if (this.audioPlayer.getPlayingTrack() != null)
        {
            this.lastTrackEndTime = Instant.now();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        LOGGER.error("Track exception: ", exception);
        this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createSongErrorMessage(track, exception)).queue();
        this.lastTrackEndTime = Instant.now();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs)
    {
        LOGGER.warn("Track stuck: {}", track.getInfo().title);
        this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createSongErrorMessage(track)).queue();
        this.lastTrackEndTime = Instant.now();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace)
    {
        LOGGER.warn("Track stuck: {}", Arrays.asList(stackTrace));
        this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createSongErrorMessage(track)).queue();
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

    public void stop()
    {
        this.audioPlayer.setPaused(true);
        this.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createPlayerStoppedMessage()).queue();
    }

    public void resume()
    {
        this.audioPlayer.setPaused(false);
        if (!isPlayingTrack())
        {
            skip();
        }

        this.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createPlayerResumedMessage()).queue();
    }

    public void setVolume(int volume)
    {
        this.audioPlayer.setVolume(volume);
        this.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createPlayerVolumeChangedMessage(volume)).queue();
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

    public AudioLoadResultHandler getAudioLoadHandler()
    {
        return this.audioLoadHandler;
    }

    private void queueLastTrackIfLoop()
    {
        AudioTrack audioTrack = this.getPlayingTrack();
        if (audioTrack != null && isLoop)
        {
            tracksQueue.add(audioTrack.makeClone());
        }
    }

    private boolean isPlayingTrack()
    {
        return this.getPlayingTrack() != null;
    }
}
