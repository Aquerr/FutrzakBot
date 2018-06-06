package io.github.aquerr.futrzakbot.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler
{
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer)
    {
        this.audioPlayer = audioPlayer;

//        audioPlayer.playTrack(new YoutubeAudioTrack(new AudioTrackInfo("Dont Let Me Be Misunderstood", "Krakow Street Band",
//                289L, "", false, "https://www.youtube.com/watch?v=CDbL51q0pNk"), new YoutubeAudioSourceManager()));
//        audioPlayer.setVolume(50);
    }

    @Override
    public boolean canProvide()
    {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio()
    {
        return lastFrame.data;
    }

    @Override
    public boolean isOpus()
    {
        return true;
    }
}
