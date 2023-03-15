package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.PlayCommand;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PlayCommandTest
{
    private static final String ADDING_TRACK_KEY = "command.play.adding";
    private static final String ADDING_TRACK = "Adding track...";
    private static final String COMMAND_DESC_KEY = "command.play.description";
    private static final String COMMAND_DESC = "Command description";
    private static final String SONG_PARAM_DESC_KEY = "command.play.slash.param.song.desc";
    private static final String SONG_PARAM_DESC = "Song param description";
    private static final String SOUNDCLOUD_PARAM_DESC_KEY = "command.play.slash.param.soundcloud.desc";
    private static final String SOUNDCLOUD_PARAM_DESC = "Soundcloud param description";

    private static final String YOUTUBE_PARAM_DESC_KEY = "command.play.slash.param.youtube.desc";
    private static final String YOUTUBE_PARAM_DESC = "YouTube param description";
    private static final String MUST_BE_ON_VOICE_CHANNEL_KEY = "error.command.must-be-on-voice-channel";
    private static final String MUST_BE_ON_VOICE_CHANNEL = "User must be on voice channel";
    private static final String SONG_PARAM_KEY = "song";
    private static final long GUILD_ID = 1L;
    private static final String SONG_NAME = "My Song Name";

    @Mock
    private GuildVoiceState guildVoiceState;
    @Mock
    private Member member;
    @Mock
    private Guild guild;
    @Mock
    private TextChannel textChannel;
    @Mock
    private MessageSource messageSource;
    @Mock
    private FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    @InjectMocks
    private PlayCommand playCommand;

    @Test
    void onSlashCommandShouldAddTrackToQueue()
    {
        SlashCommandEvent slashCommandEvent = mock(SlashCommandEvent.class);
        OptionMapping optionMapping = mock(OptionMapping.class);
        VoiceChannel voiceChannel = mock(VoiceChannel.class);

        given(optionMapping.getAsString()).willReturn(SONG_NAME);
        given(slashCommandEvent.getOption(SONG_PARAM_KEY)).willReturn(optionMapping);
        given(slashCommandEvent.getGuild()).willReturn(guild);
        given(slashCommandEvent.getMember()).willReturn(member);
        given(slashCommandEvent.getTextChannel()).willReturn(textChannel);
        given(member.getVoiceState()).willReturn(guildVoiceState);
        given(guildVoiceState.getChannel()).willReturn(voiceChannel);
        given(slashCommandEvent.reply(anyString())).willReturn(mock(ReplyAction.class));
        given(messageSource.getMessage(ADDING_TRACK_KEY)).willReturn(ADDING_TRACK);

        playCommand.onSlashCommand(slashCommandEvent);

        verify(slashCommandEvent, times(1)).reply(ADDING_TRACK);
        verify(futrzakAudioPlayerManager, times(1)).queue(guild, textChannel, voiceChannel, member, "scsearch: " + SONG_NAME, true);
    }

    @Test
    void onSlashCommandShouldReturnMustBeInVoiceChannelErrorWhenMemberIsNotInVoiceChannel()
    {
        SlashCommandEvent slashCommandEvent = mock(SlashCommandEvent.class);
        OptionMapping optionMapping = mock(OptionMapping.class);

        given(optionMapping.getAsString()).willReturn(SONG_NAME);
        given(slashCommandEvent.getOption(SONG_PARAM_KEY)).willReturn(optionMapping);
        given(slashCommandEvent.getMember()).willReturn(member);
        given(slashCommandEvent.reply(anyString())).willReturn(mock(ReplyAction.class));
        given(messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL_KEY)).willReturn(MUST_BE_ON_VOICE_CHANNEL);
        given(member.getVoiceState()).willReturn(guildVoiceState);
        given(guildVoiceState.getChannel()).willReturn(null);

        playCommand.onSlashCommand(slashCommandEvent);

        verify(slashCommandEvent, times(1)).reply(MUST_BE_ON_VOICE_CHANNEL);
        verify(messageSource, times(1)).getMessage(MUST_BE_ON_VOICE_CHANNEL_KEY);
        verifyNoInteractions(futrzakAudioPlayerManager);
    }

    @Test
    void slashCommandDataContainsCorrectName()
    {
        given(messageSource.getMessage(COMMAND_DESC_KEY)).willReturn(COMMAND_DESC);
        given(messageSource.getMessage(SONG_PARAM_DESC_KEY)).willReturn(SONG_PARAM_DESC);
        given(messageSource.getMessage(SOUNDCLOUD_PARAM_DESC_KEY)).willReturn(SOUNDCLOUD_PARAM_DESC);
        given(messageSource.getMessage(YOUTUBE_PARAM_DESC_KEY)).willReturn(YOUTUBE_PARAM_DESC);

        assertThat(playCommand.getSlashCommandData().getName()).isEqualTo("play");
    }

    @Test
    void slashCommandDataGetsDescriptionFormMessageSource()
    {
        given(messageSource.getMessage(COMMAND_DESC_KEY)).willReturn(COMMAND_DESC);
        given(messageSource.getMessage(SONG_PARAM_DESC_KEY)).willReturn(SONG_PARAM_DESC);
        given(messageSource.getMessage(SOUNDCLOUD_PARAM_DESC_KEY)).willReturn(SOUNDCLOUD_PARAM_DESC);
        given(messageSource.getMessage(YOUTUBE_PARAM_DESC_KEY)).willReturn(YOUTUBE_PARAM_DESC);

        assertThat(playCommand.getSlashCommandData().getDescription()).isEqualTo(COMMAND_DESC);
    }

    @Test
    void slashCommandDataContainsCorrectParameters()
    {
        given(messageSource.getMessage(COMMAND_DESC_KEY)).willReturn(COMMAND_DESC_KEY);
        given(messageSource.getMessage(SONG_PARAM_DESC_KEY)).willReturn(SONG_PARAM_DESC);
        given(messageSource.getMessage(SOUNDCLOUD_PARAM_DESC_KEY)).willReturn(SOUNDCLOUD_PARAM_DESC);
        given(messageSource.getMessage(YOUTUBE_PARAM_DESC_KEY)).willReturn(YOUTUBE_PARAM_DESC);

        assertThat(playCommand.getSlashCommandData().getOptions()).hasSize(3);
        List<OptionData> optionDataList = playCommand.getSlashCommandData().getOptions();
        assertWith(optionDataList.get(0), (optionData) -> {
            assertThat(optionData.getType()).isSameAs(OptionType.STRING);
            assertThat(optionData.getName()).isEqualTo(SONG_PARAM_KEY);
            assertThat(optionData.getDescription()).isEqualTo(SONG_PARAM_DESC);
            assertThat(optionData.isRequired()).isFalse();
        });
    }
}
