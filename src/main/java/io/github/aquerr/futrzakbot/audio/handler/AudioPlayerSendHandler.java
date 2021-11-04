package io.github.aquerr.futrzakbot.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

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

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio()
    {
        return ByteBuffer.wrap(this.lastFrame.getData());
    }

    @Override
    public boolean isOpus()
    {
        return true;
    }
}
