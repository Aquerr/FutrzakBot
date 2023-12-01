package io.github.aquerr.futrzakbot.discord.command;

import com.google.common.base.Verify;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JumpToCommandTest {

    private static final String JUMPTO_COMMAND_NAME_KEY = "command.jumpto.name";
    private static final String JUMPTO_COMMAND_DESCRIPTION_KEY = "command.jumpto.description";
    private static final String JUMPTO_COMMAND_NAME = "Jumpto command name";
    private static final String COMMAND_JUMPTO_DESCRIPTION = "Jumpto command description";
    private static final String JUMPING_TO = "Jumping to...";
    private static final String JUMPING_TO_KEY = "command.jumpto.change";
    private static final int DESIRED_TIME = 2137;
    private static final String DESIRED_TIME_WITH_COLON = "21:37";
    

    @Mock
    private MessageSource messageSource;
    @Mock
    private Guild guild;
    @Mock
    private TextChannel textChannel;
    @Mock
    private MessageChannelUnion messageChannelUnion;
    @Mock
    private FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    @Mock
    private ReplyCallbackAction replyCallbackAction;

    @InjectMocks
    private JumpToCommand jumptoCommand;
    
    @Test
    void getNameShouldReturnCorrectName()
    {
        given(messageSource.getMessage(JUMPTO_COMMAND_NAME_KEY)).willReturn(JUMPTO_COMMAND_NAME);

        assertThat(jumptoCommand.getName()).isEqualTo(JUMPTO_COMMAND_NAME);
    }

    @Test
    void getDescriptionShouldReturnCorrectDescription()
    {
        given(messageSource.getMessage(JUMPTO_COMMAND_DESCRIPTION_KEY)).willReturn(COMMAND_JUMPTO_DESCRIPTION);

        assertThat(jumptoCommand.getDescription()).isEqualTo(COMMAND_JUMPTO_DESCRIPTION);
    }

    @Test
    void onSlashCommandShouldJumpToSpecifiedTime()
    {
        SlashCommandInteractionEvent slashCommandEvent = mock(SlashCommandInteractionEvent.class);
        OptionMapping optionMapping = mock(OptionMapping.class);

        given(messageSource.getMessage(JUMPING_TO_KEY)).willReturn(JUMPING_TO);
        given(messageSource.getMessage("command.jumpto.change")).willReturn(JUMPING_TO);
        given(slashCommandEvent.getOption("jumpto")).willReturn(optionMapping);
        given(optionMapping.getAsString()).willReturn(String.valueOf(DESIRED_TIME));
        given(optionMapping.getAsInt()).willReturn(DESIRED_TIME);
        given(slashCommandEvent.getGuild()).willReturn(guild);
        given(slashCommandEvent.getChannel()).willReturn(messageChannelUnion);
        given(messageChannelUnion.asTextChannel()).willReturn(textChannel);
        given(slashCommandEvent.reply(JUMPING_TO)).willReturn(replyCallbackAction);

        jumptoCommand.onSlashCommand(slashCommandEvent);

        verify(slashCommandEvent).reply(JUMPING_TO);
        verify(futrzakAudioPlayerManager).jumpTo(guild.getIdLong(), textChannel, DESIRED_TIME);

    }

    @Test
    void onSlashCommandShouldJumpToSpecifiedTimeWithColon()
    {
        SlashCommandInteractionEvent slashCommandEvent = mock(SlashCommandInteractionEvent.class);
        OptionMapping optionMapping = mock(OptionMapping.class);

        given(messageSource.getMessage(JUMPING_TO_KEY)).willReturn(JUMPING_TO);
        given(messageSource.getMessage("command.jumpto.change")).willReturn(JUMPING_TO);
        given(slashCommandEvent.getOption("jumpto")).willReturn(optionMapping);
        given(optionMapping.getAsString()).willReturn(DESIRED_TIME_WITH_COLON);
        given(slashCommandEvent.getGuild()).willReturn(guild);
        given(slashCommandEvent.getChannel()).willReturn(messageChannelUnion);
        given(messageChannelUnion.asTextChannel()).willReturn(textChannel);
        given(slashCommandEvent.reply(JUMPING_TO)).willReturn(replyCallbackAction);

        jumptoCommand.onSlashCommand(slashCommandEvent);

        verify(slashCommandEvent).reply(JUMPING_TO);
        verify(futrzakAudioPlayerManager).jumpTo(guild.getIdLong(), textChannel, 1297);

    }
 }