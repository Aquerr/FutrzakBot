package io.github.aquerr.futrzakbot.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FutrzakAudioPlayerTest
{
    private static final long GUILD_ID = 1L;

    private FutrzakAudioPlayer futrzakAudioPlayer;

    @BeforeEach
    void setUp()
    {
        FutrzakBot futrzakBot = mock(FutrzakBot.class);
        AudioPlayer audioPlayer = mock(AudioPlayer.class);
        FutrzakMessageEmbedFactory futrzakMessageEmbedFactory = mock(FutrzakMessageEmbedFactory.class);
        futrzakAudioPlayer = new FutrzakAudioPlayer(futrzakBot, GUILD_ID, audioPlayer, futrzakMessageEmbedFactory);
    }

    @Test
    void connectToVoiceChannelShouldThrowNullPointerExceptionWhenVoiceChannelNotProvided()
    {
        assertThrows(NullPointerException.class, () -> futrzakAudioPlayer.connectToVoiceChannel(null));
    }

    @Test
    void connectToVoiceChannelShouldConnectToVoiceChannel()
    {
        Guild guild = mock(Guild.class);
        AudioManager audioManager = mock(AudioManager.class);
        VoiceChannel voiceChannel = mock(VoiceChannel.class);
        given(voiceChannel.getGuild()).willReturn(guild);
        given(guild.getAudioManager()).willReturn(audioManager);

        futrzakAudioPlayer.connectToVoiceChannel(voiceChannel);

        verify(audioManager).setSendingHandler(any());
        assertThat(futrzakAudioPlayer.getVoiceChannel()).isEqualTo(voiceChannel);
    }
}