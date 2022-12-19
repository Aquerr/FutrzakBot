package io.github.aquerr.futrzakbot.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayer;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;

public class FutrzakAudioLoadHandler implements AudioLoadResultHandler
{
    protected final FutrzakAudioPlayer futrzakAudioPlayer;
    protected final FutrzakMessageEmbedFactory messageEmbedFactory;

    public FutrzakAudioLoadHandler(FutrzakAudioPlayer futrzakAudioPlayer, FutrzakMessageEmbedFactory futrzakMessageEmbedFactory)
    {
        this.futrzakAudioPlayer = futrzakAudioPlayer;
        this.messageEmbedFactory = futrzakMessageEmbedFactory;
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        this.futrzakAudioPlayer.queue(track, true);
        this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        if (playlist.isSearchResult())
        {
            AudioTrack track = playlist.getTracks().get(0);
            this.futrzakAudioPlayer.queue(track, true);
            this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
        }
        else
        {
            for (AudioTrack track : playlist.getTracks())
            {
                this.futrzakAudioPlayer.queue(track, true);
            }
            this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createPlaylistAddedToQueueMessage(playlist)).complete();
        }
    }

    @Override
    public void noMatches()
    {
        this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongNotFoundMessage()).complete();
    }

    @Override
    public void loadFailed(FriendlyException exception)
    {
        this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongLoadFailedMessage(exception.getLocalizedMessage())).complete();
    }
}
