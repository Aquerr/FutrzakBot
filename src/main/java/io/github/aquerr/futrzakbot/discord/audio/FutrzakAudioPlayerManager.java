package io.github.aquerr.futrzakbot.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.audio.handler.FutrzakQueueAndDontPlayLoadHandler;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class FutrzakAudioPlayerManager
{
    private static final String PROTOCOL_REGEX = "^(http://)|(https://).*$";

    private final FutrzakBot futrzakBot;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, FutrzakAudioPlayer> guildAudioPlayers = new ConcurrentHashMap<>();
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    public FutrzakAudioPlayerManager(FutrzakBot futrzakBot, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.futrzakBot = futrzakBot;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        this.messageEmbedFactory = messageEmbedFactory;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::botKickTaskRun, 5, 5, TimeUnit.MINUTES);
    }

    public void queue(long guildId, TextChannel textChannel, VoiceChannel voiceChannel, String trackName, boolean shouldStartPlaying)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.setVoiceChannel(voiceChannel);

        if (shouldStartPlaying)
        {
            this.audioPlayerManager.loadItem(getYoutTubeAudioIdentifierForTrack(trackName), futrzakAudioPlayer.getAudioLoadHandler());
        }
        else
        {
            this.audioPlayerManager.loadItem(getYoutTubeAudioIdentifierForTrack(trackName), new FutrzakQueueAndDontPlayLoadHandler(futrzakAudioPlayer, messageEmbedFactory));
        }
    }

    public void skipAndPlayNextTrack(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        AudioTrack audioTrack = futrzakAudioPlayer.getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(messageEmbedFactory.createSkipTrackMessage(audioTrack)).queue();
        }

        futrzakAudioPlayer.skip();
    }

    public boolean toggleLoop(long guildId,TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        return futrzakAudioPlayer.toggleLoop();
    }

    public void clearQueue(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.clear();
    }

    public void removeElement(int element, long guildId,TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        if (0 < element && element <= futrzakAudioPlayer.getQueue().size())
        {
            AudioTrack audioTrack = futrzakAudioPlayer.remove(element);
            textChannel.sendMessageEmbeds(messageEmbedFactory.createRemoveMessage(element, audioTrack)).queue();
        }
        else
        {
            textChannel.sendMessageEmbeds(messageEmbedFactory.createOutOfRangeMessage()).queue();
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
        return this.guildAudioPlayers.computeIfAbsent(guildId, id -> new FutrzakAudioPlayer(id, this.audioPlayerManager.createPlayer(), messageEmbedFactory));
    }

    private void botKickTaskRun()
    {
        final Iterator<Map.Entry<Long, FutrzakAudioPlayer>> futrzakAudioPlayersIterator = this.guildAudioPlayers.entrySet().iterator();
        while (futrzakAudioPlayersIterator.hasNext())
        {
            Map.Entry<Long, FutrzakAudioPlayer> futrzakAudioPlayerEntry = futrzakAudioPlayersIterator.next();
            FutrzakAudioPlayer futrzakAudioPlayer = futrzakAudioPlayerEntry.getValue();

            if (futrzakAudioPlayer.getVoiceChannel().getMembers().size() > 0 && futrzakAudioPlayer.isActive())
                continue;

            this.futrzakBot.getJda().getGuildById(futrzakAudioPlayerEntry.getKey()).getAudioManager().closeAudioConnection();
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
