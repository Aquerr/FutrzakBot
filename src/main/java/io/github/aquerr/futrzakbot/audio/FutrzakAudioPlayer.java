package io.github.aquerr.futrzakbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;

public class FutrzakAudioPlayer
{
    private static FutrzakAudioPlayer INSTANCE;

    public static FutrzakAudioPlayer getInstance()
    {
        return FutrzakAudioPlayerInstanceHolder.INSTANCE;
    }

    private static class FutrzakAudioPlayerInstanceHolder
    {
        private static final FutrzakAudioPlayer INSTANCE = new FutrzakAudioPlayer();
    }

    private final AudioPlayerManager audioPlayerManager;
    private final AudioPlayer audioPlayer;
    private final AudioPlayerSendHandler audioPlayerSendHandler;
    private final TrackScheduler trackScheduler;

    public FutrzakAudioPlayer()
    {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        this.audioPlayer = this.audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayerSendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    public void queue(TextChannel textChannel, String trackName)
    {
        this.audioPlayerManager.loadItem("ytsearch: " + trackName, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                trackScheduler.queue(track);
                textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createSongAddedToQueueMessage(track.getInfo().author, track.getInfo().title)).complete();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack track = playlist.getTracks().get(0);
                trackScheduler.queue(track);
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
        });
    }

    public void playTrack(String trackName)
    {
        this.audioPlayerManager.loadItem("ytsearch: " + trackName, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                trackScheduler.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                for (final AudioTrack audioTrack : playlist.getTracks())
                {
                    trackScheduler.queue(audioTrack);
                }
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
        });
    }

    public void playNextTrack(TextChannel textChannel)
    {
        this.trackScheduler.playNextTrack();
        AudioTrack track = this.audioPlayer.getPlayingTrack();
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createNowPlayingMessage(track.getInfo().author, track.getInfo().title)).complete();
    }

    public void stop()
    {
        this.audioPlayer.stopTrack();
    }

    public void resume()
    {
        this.audioPlayer.setPaused(!this.audioPlayer.isPaused());
    }

    public void setVolume(int volume)
    {
        this.audioPlayer.setVolume(volume);
    }

    public AudioPlayer getAudioPlayer()
    {
        return audioPlayer;
    }
}
