package io.github.aquerr.futrzakbot.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.discord.audio.handler.FutrzakAudioLoadHandler;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;


public class FutrzakAudioPlayer extends AudioEventAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakAudioPlayer.class);

    private final FutrzakBot futrzakBot;
    private final long guildId;
    private boolean isLoop = false;
    private final FutrzakAudioLoadHandler audioLoadHandler;
    private final AudioPlayer audioPlayer;
    private final LinkedList<AudioTrack> tracksQueue = new LinkedList<>();
    private final FutrzakMessageEmbedFactory messageEmbedFactory;
    private TextChannel lastBotUsageChannel;
    private VoiceChannel voiceChannel;
    private Instant lastActiveTime = Instant.now();

    public FutrzakAudioPlayer(final FutrzakBot futrzakBot, long guildId, AudioPlayer audioPlayer, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.futrzakBot = futrzakBot;
        this.guildId = guildId;
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.audioLoadHandler = new FutrzakAudioLoadHandler(this, messageEmbedFactory);
        this.messageEmbedFactory = FutrzakMessageEmbedFactory.getInstance();
    }

    public void setLastBotUsageChannel(TextChannel lastBotUsageChannel)
    {
        this.lastBotUsageChannel = lastBotUsageChannel;
    }

    public void connectToVoiceChannel(@Nonnull VoiceChannel voiceChannel)
    {
        this.voiceChannel = voiceChannel;
        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
        if (audioManager.getSendingHandler() == null)
        {
            audioManager.setSendingHandler(new AudioPlayerSendHandler(this.getInternalAudioPlayer()));
        }
        audioManager.openAudioConnection(voiceChannel);
    }

    public VoiceChannel getVoiceChannel()
    {
        return voiceChannel;
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
        updateLastActiveTime();
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
        LOGGER.info("Music player for guildId = {} has been paused!", this.guildId);
    }

    @Override
    public void onPlayerResume(AudioPlayer player)
    {
        LOGGER.info("Music player for guildId = {} has been resumed!", this.guildId);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        LOGGER.info("Music player for guildId = {} has started new track!", this.guildId);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        LOGGER.info("Music player for guildId = {} has ended track!", this.guildId);
        if (endReason.mayStartNext)
        {
            skip();
        }

        if (isLoop)
        {
            queue(track.makeClone(), true);
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        if (LOGGER.isWarnEnabled())
            LOGGER.warn(format("Music player for guildId = %s has caught an exception!", this.guildId), exception);

        this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createSongErrorMessage(track, exception)).queue();
        updateLastActiveTime();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs)
    {
        LOGGER.info("Music player for guildId = {} got stuck track = {}", this.guildId, track.getInfo().title);
        this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createSongErrorMessage(track)).queue();
        updateLastActiveTime();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace)
    {
        LOGGER.info("Music player for guildId = {} got stuck track = {}", this.guildId, Arrays.asList(stackTrace));
        this.lastBotUsageChannel.sendMessageEmbeds(messageEmbedFactory.createSongErrorMessage(track)).queue();
        updateLastActiveTime();
    }

    public long getGuildId()
    {
        return guildId;
    }

    public AudioTrack getPlayingTrack()
    {
        return this.audioPlayer.getPlayingTrack();
    }

    public void pause()
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
        updateLastActiveTime();
    }

    public AudioPlayer getInternalAudioPlayer()
    {
        return audioPlayer;
    }

    private Instant getLastActiveTime()
    {
        return lastActiveTime;
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

    private void updateLastActiveTime()
    {
        this.lastActiveTime = Instant.now();
    }

    public boolean isActive()
    {
        if (!isConnectedToVoiceChannel())
            return false;

        if (getListeningMembersCount() <= 1)
            return false;

        if (isPlayingTrack() && !audioPlayer.isPaused())
            return true;

        if (getLastActiveTime().plus(5, ChronoUnit.MINUTES).isBefore(Instant.now()))
            return true;

        return false;
    }

    public boolean isConnectedToVoiceChannel()
    {
        return this.voiceChannel != null;
    }

    private int getListeningMembersCount()
    {
        return Optional.ofNullable(this.voiceChannel)
                .map(GuildChannel::getMembers)
                .map(List::size)
                .orElse(0);
    }

    public void disconnectFromVoiceChannel()
    {
        this.voiceChannel = null;
        this.futrzakBot.getJda().getGuildById(this.guildId).getAudioManager().closeAudioConnection();
    }

    public void stop()
    {
        clear();
        this.audioPlayer.playTrack(null);
    }
}
