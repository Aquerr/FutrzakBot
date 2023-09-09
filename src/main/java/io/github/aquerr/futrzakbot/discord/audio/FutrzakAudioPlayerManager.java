package io.github.aquerr.futrzakbot.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.audio.handler.FutrzakAudioLoadHandler;
import io.github.aquerr.futrzakbot.discord.audio.handler.FutrzakQueueAndDontPlayLoadHandler;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

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
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        this.messageEmbedFactory = messageEmbedFactory;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::botKickTaskRun, 1, 5, TimeUnit.MINUTES);
    }

    public void queue(Guild guild, TextChannel textChannel, VoiceChannel voiceChannel, Member member, String trackIdentifier, boolean shouldStartPlaying)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guild.getIdLong());
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
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
            textChannel.sendMessageEmbeds(messageEmbedFactory.createWrongTrackPositionMessage()).queue();
        }
    }

    public void stop(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.pause();
        futrzakAudioPlayer.stopCurrentTrack();
    }

    public void resume(long guildId, TextChannel textChannel, VoiceChannel voiceChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.connectToVoiceChannel(voiceChannel);
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

    public void pause(long guildId, TextChannel textChannel)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = getOrCreateAudioPlayer(guildId);
        futrzakAudioPlayer.setLastBotUsageChannel(textChannel);
        futrzakAudioPlayer.pause();
    }
}
