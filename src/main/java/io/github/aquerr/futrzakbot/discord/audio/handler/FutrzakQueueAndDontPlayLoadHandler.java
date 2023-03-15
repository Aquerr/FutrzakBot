package io.github.aquerr.futrzakbot.discord.audio.handler;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAdditionalAudioTrackData;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayer;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;

public class FutrzakQueueAndDontPlayLoadHandler extends FutrzakAudioLoadHandler
{
    public FutrzakQueueAndDontPlayLoadHandler(FutrzakAudioPlayer futrzakAudioPlayer, FutrzakMessageEmbedFactory messageEmbedFactory, FutrzakAdditionalAudioTrackData futrzakAdditionalAudioTrackData)
    {
        super(futrzakAudioPlayer, messageEmbedFactory, futrzakAdditionalAudioTrackData);
    }

    @Override
    protected boolean shouldStartPlayingLoadedTrack()
    {
        return false;
    }
}
