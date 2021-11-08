package io.github.aquerr.futrzakbot.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayer;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;

public class FutrzakAudioLoadHandler implements AudioLoadResultHandler
{
    private final long guildId;
    private final FutrzakAudioPlayer futrzakAudioPlayer;
    private final TextChannel textChannel;

    public FutrzakAudioLoadHandler(long guildId, FutrzakAudioPlayer futrzakAudioPlayer, TextChannel textChannel)
    {
        this.guildId = guildId;
        this.futrzakAudioPlayer = futrzakAudioPlayer;
        this.textChannel = textChannel;
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        this.futrzakAudioPlayer.queue(track);
        this.textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        AudioTrack track = playlist.getTracks().get(0);
        this.futrzakAudioPlayer.queue(track);
        this.textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void noMatches()
    {
        this.textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongNotFoundMessage());
    }

    @Override
    public void loadFailed(FriendlyException exception)
    {
        this.textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongLoadFailedMessage(exception.getLocalizedMessage()));
    }

    public long getGuildId()
    {
        return guildId;
    }
}
