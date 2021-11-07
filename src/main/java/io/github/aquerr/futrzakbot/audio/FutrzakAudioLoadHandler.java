package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
        futrzakAudioPlayer.queue(track);
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        AudioTrack track = playlist.getTracks().get(0);
        futrzakAudioPlayer.queue(track);
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    @Override
    public void noMatches()
    {
        System.out.println("Song not found!");
    }

    @Override
    public void loadFailed(FriendlyException exception)
    {
        exception.printStackTrace();
    }
}
