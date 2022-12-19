package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class HelpCommandTest
{
    private static final String COMMAND_NAME_KEY = "command.help.name";
    private static final String COMMAND_DESCRIPTION_KEY = "command.help.description";
    private static final String COMMAND_NAME = "help";
    private static final String COMMAND_DESCRIPTION = "description";

    @Mock
    private CommandManager commandManager;

    @Mock
    private MessageSource messageSource;
    @Mock
    private FutrzakMessageEmbedFactory messageEmbedFactory;

    @InjectMocks
    private HelpCommand helpCommand;

    @BeforeEach
    public void setUp()
    {
        openMocks(this);
    }

    @Test
    void executeShouldGetCommandsFromCommandManagerAndBuildHelpMessage()
    {
        // given
        MessageEmbed helpMessageEmbed = new EmbedBuilder().setDescription("Test Help").build();
        CommandContext context = mock(CommandContext.class);
        TextChannel textChannel = mock(TextChannel.class);
        MessageAction messageAction = mock(MessageAction.class);
        Message message = mock(Message.class);
        given(context.getTextChannel()).willReturn(textChannel);
        given(textChannel.sendMessageEmbeds(any(MessageEmbed.class))).willReturn(messageAction);
        given(messageAction.complete()).willReturn(message);
        given(message.addReaction(anyString())).willReturn(mock(RestAction.class));
        given(messageEmbedFactory.createHelpMessage(any(), anyInt())).willReturn(helpMessageEmbed);

        // when
        boolean result = helpCommand.execute(context);

        // then
        assertThat(result).isTrue();
        verify(commandManager).getCommands();
        verify(textChannel).sendMessageEmbeds(helpMessageEmbed);
        verify(messageAction).complete();
        verify(message).addReaction(EmojiUnicodes.ARROW_LEFT);
        verify(message).addReaction(EmojiUnicodes.ARROW_RIGHT);
    }

    @Test
    void getAliasesShouldReturnListWithHelpAlias()
    {
        // given
        // when
        List<String> aliases = helpCommand.getAliases();

        // then
        assertThat(aliases).containsExactlyElementsOf(Arrays.asList("help"));
    }

    @Test
    void getNameShouldUseMessageSourceAndReturnCommandName()
    {
        // given
        given(messageSource.getMessage(COMMAND_NAME_KEY)).willReturn(COMMAND_NAME);

        // when
        String result = helpCommand.getName();

        // then
        assertThat(result).isEqualTo(COMMAND_NAME);
    }

    @Test
    void getDescriptionShouldUseMessageSourceAndReturnCommandDescription()
    {
        // given
        given(messageSource.getMessage(COMMAND_DESCRIPTION_KEY)).willReturn(COMMAND_DESCRIPTION);

        // when
        String result = helpCommand.getDescription();

        // then
        assertThat(result).isEqualTo(COMMAND_DESCRIPTION);
    }

    @Test
    void getParametersShouldReturnEmptyList()
    {
        // given
        // when
        List<Parameter<?>> parameters = helpCommand.getParameters();

        // then
        assertThat(parameters).isEmpty();
    }
}