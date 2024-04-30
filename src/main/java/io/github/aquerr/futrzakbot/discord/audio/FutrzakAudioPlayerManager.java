package io.github.aquerr.futrzakbot.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.audio.handler.FutrzakAudioLoadHandler;
import io.github.aquerr.futrzakbot.discord.audio.handler.FutrzakQueueAndDontPlayLoadHandler;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class FutrzakAudioPlayerManager
{
    private final FutrzakBot futrzakBot;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, FutrzakAudioPlayer> guildAudioPlayers = new ConcurrentHashMap<>();
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    public FutrzakAudioPlayerManager(FutrzakBot futrzakBot, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.futrzakBot = futrzakBot;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        this.messageEmbedFactory = messageEmbedFactory;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::botKickTaskRun, 1, 5, TimeUnit.MINUTES);
    }

    public void queue(Guild guild, GuildMessageChannel channel, VoiceChannel voiceChannel, Member member, String trackIdentifier, boolean shouldStartPlaying)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guild.getIdLong());
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.connectToVoiceChannel(voiceChannel);

        FutrzakAdditionalAudioTrackData futrzakAdditionalAudioTrackData = new FutrzakAdditionalAudioTrackData(member);

        if (shouldStartPlaying)
        {
            this.audioPlayerManager.loadItem(trackIdentifier, new FutrzakAudioLoadHandler(futrzakAudioPlayer, messageEmbedFactory, futrzakAdditionalAudioTrackData));
        }
        else
        {
            this.audioPlayerManager.loadItem(trackIdentifier, new FutrzakQueueAndDontPlayLoadHandler(futrzakAudioPlayer, messageEmbedFactory, futrzakAdditionalAudioTrackData));
        }
    }

    public void skipAndPlayNextTrack(long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        AudioTrack audioTrack = futrzakAudioPlayer.getPlayingTrack();
        if (audioTrack != null)
        {
            channel.sendMessageEmbeds(messageEmbedFactory.createSkipTrackMessage(audioTrack)).queue();
        }

        futrzakAudioPlayer.skip();
    }

    public boolean toggleLoop(long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        return futrzakAudioPlayer.toggleLoop();
    }

    public void clearQueue(long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.clear();
    }

    public void removeElement(int trackPosition, long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        if (0 < trackPosition && trackPosition <= futrzakAudioPlayer.getQueue().size())
        {
            AudioTrack audioTrack = futrzakAudioPlayer.remove(trackPosition);
            channel.sendMessageEmbeds(messageEmbedFactory.createRemoveMessage(trackPosition, audioTrack)).queue();
        }
        else
        {
            channel.sendMessageEmbeds(messageEmbedFactory.createWrongTrackPositionMessage()).queue();
        }
    }

    public void removeElement(String trackName, long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);

        List<AudioTrack> tracks = getQueue(guildId);
        int index = 0;
        for (; index < tracks.size() - 1; index++)
        {
            AudioTrack audioTrack = tracks.get(index);
            if (matchesName(audioTrack, trackName))
                break;
        }

        int trackPosition = index + 1;
        AudioTrack removedTrack = futrzakAudioPlayer.remove(trackPosition);

        if (removedTrack != null)
        {
            channel.sendMessageEmbeds(messageEmbedFactory.createRemoveMessage(trackPosition, removedTrack)).queue();
        }
    }

    private boolean matchesName(AudioTrack track, String name)
    {
        name = name.toLowerCase();
        AudioTrackInfo info = track.getInfo();
        String trackName = info.title.toLowerCase() + " - " + info.author.toLowerCase();
        return trackName.startsWith(name)
                || trackName.contains(name);
    }

    public void stop(long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.pause();
        futrzakAudioPlayer.stopCurrentTrack();
    }

    public void resume(long guildId, GuildMessageChannel channel, VoiceChannel voiceChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.connectToVoiceChannel(voiceChannel);
        futrzakAudioPlayer.resume();
    }

    public void setVolume(long guildId, GuildMessageChannel channel, int volume)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.setVolume(volume);
    }

    public FutrzakAudioPlayer getOrCreateAudioPlayer(long guildId)
    {
        return this.guildAudioPlayers.computeIfAbsent(guildId, id -> new FutrzakAudioPlayer(futrzakBot, id, this.audioPlayerManager.createPlayer(), messageEmbedFactory));
    }

    private void botKickTaskRun()
    {
        try
        {
            final Iterator<Map.Entry<Long, FutrzakAudioPlayer>> futrzakAudioPlayersIterator = this.guildAudioPlayers.entrySet().iterator();
            while (futrzakAudioPlayersIterator.hasNext())
            {
                Map.Entry<Long, FutrzakAudioPlayer> futrzakAudioPlayerEntry = futrzakAudioPlayersIterator.next();
                FutrzakAudioPlayer futrzakAudioPlayer = futrzakAudioPlayerEntry.getValue();

                if (futrzakAudioPlayer.isActive())
                    continue;

                if (futrzakAudioPlayer.isConnectedToVoiceChannel())
                {
                    log.info("Kicking bot due to inactive music player or no members in voice channel. Guild = {}, VoiceChannel = {}", futrzakAudioPlayer.getGuildId(), futrzakAudioPlayer.getVoiceChannel().getName());
                    disconnect(futrzakAudioPlayer.getGuildId());
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
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

    public void disconnect(long guildId)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = this.guildAudioPlayers.get(guildId);
        if (futrzakAudioPlayer.isConnectedToVoiceChannel())
        {
            futrzakAudioPlayer.stop();
            futrzakAudioPlayer.disconnectFromVoiceChannel();
            this.futrzakBot.getJda().getGuildById(guildId).getAudioManager().closeAudioConnection();
        }
    }

    public void pause(long guildId, GuildMessageChannel channel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.pause();
    }

    public void jumpTo(long guildId, GuildMessageChannel channel, int time) {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(channel);
        futrzakAudioPlayer.jumpTo((long)time);
    }
}
