package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
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
    private static final String PROTOCOL_REGEX = "^(http://)|(https://).*$";

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
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);

        this.audioPlayerManager.loadItem(getYoutTubeAudioIdentifierForTrack(trackName), futrzakAudioPlayer.getAudioLoadHandler());
    }

    public void skipAndPlayNextTrack(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        AudioTrack audioTrack = futrzakAudioPlayer.getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSkipTrackMessage(audioTrack)).queue();
        }

        futrzakAudioPlayer.skip();
        audioTrack = futrzakAudioPlayer.getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createNowPlayingMessage(audioTrack)).queue();
        }
    }

    public void stop(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.stop();
    }

    public void resume(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.resume();
    }

    public void setVolume(long guildId, TextChannel textChannel, int volume)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.setVolume(volume);
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

    public void registerAudioPlayersForGuilds(List<Guild> guilds)
    {
        guilds.stream()
                .map(ISnowflake::getIdLong)
                .forEach(this::getOrCreateAudioPlayer);
    }

    private String getYoutTubeAudioIdentifierForTrack(String trackName)
    {
        if (trackName.matches(PROTOCOL_REGEX))
        {
            return trackName;
        }
        return "ytsearch: " + trackName;
    }
}
