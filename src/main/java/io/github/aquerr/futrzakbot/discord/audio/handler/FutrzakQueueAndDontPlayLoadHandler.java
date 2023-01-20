package io.github.aquerr.futrzakbot.discord.audio.handler;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayer;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;

public class FutrzakQueueAndDontPlayLoadHandler extends FutrzakAudioLoadHandler
{
    public FutrzakQueueAndDontPlayLoadHandler(FutrzakAudioPlayer futrzakAudioPlayer, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        super(futrzakAudioPlayer, messageEmbedFactory);
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        futrzakAudioPlayer.queue(track, false);
        this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        if (playlist.isSearchResult())
        {
            AudioTrack track = playlist.getTracks().get(0);
            this.futrzakAudioPlayer.queue(track, false);
            this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
        }
        else
        {
            for (AudioTrack track : playlist.getTracks())
            {
                this.futrzakAudioPlayer.queue(track, false);
            }
            this.futrzakAudioPlayer.getLastBotUsageChannel().sendMessageEmbeds(messageEmbedFactory.createPlaylistAddedToQueueMessage(playlist)).complete();
        }
    }
}
