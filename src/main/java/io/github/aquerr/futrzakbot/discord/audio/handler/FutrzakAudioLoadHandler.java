package io.github.aquerr.futrzakbot.discord.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAdditionalAudioTrackData;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayer;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;

public class FutrzakAudioLoadHandler implements AudioLoadResultHandler
{
    protected final FutrzakAudioPlayer futrzakAudioPlayer;
    protected final FutrzakMessageEmbedFactory messageEmbedFactory;
    protected final FutrzakAdditionalAudioTrackData futrzakAdditionalAudioTrackData;

    public FutrzakAudioLoadHandler(FutrzakAudioPlayer futrzakAudioPlayer, FutrzakMessageEmbedFactory futrzakMessageEmbedFactory, FutrzakAdditionalAudioTrackData futrzakAdditionalAudioTrackData)
    {
        this.futrzakAudioPlayer = futrzakAudioPlayer;
        this.messageEmbedFactory = futrzakMessageEmbedFactory;
        this.futrzakAdditionalAudioTrackData = futrzakAdditionalAudioTrackData;
    }

    protected boolean shouldStartPlayingLoadedTrack()
    {
        return true;
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        track.setUserData(futrzakAdditionalAudioTrackData);
        this.futrzakAudioPlayer.queue(track, shouldStartPlayingLoadedTrack());
        this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        if (playlist.isSearchResult())
        {
            AudioTrack track = playlist.getTracks().get(0);
            track.setUserData(futrzakAdditionalAudioTrackData);
            this.futrzakAudioPlayer.queue(track, shouldStartPlayingLoadedTrack());
            this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
        }
        else
        {
            for (AudioTrack track : playlist.getTracks())
            {
                track.setUserData(futrzakAdditionalAudioTrackData);
                this.futrzakAudioPlayer.queue(track, shouldStartPlayingLoadedTrack());
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
        this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.error(exception.getLocalizedMessage())).complete();
    }
}
