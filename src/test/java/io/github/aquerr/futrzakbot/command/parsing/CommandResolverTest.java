package io.github.aquerr.futrzakbot.command.parsing;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

class CommandResolverTest
{
    private static final String COMMAND_ALIAS = "command";
    private static final String SUB_COMMAND_ALIAS_1 = "subcommand1";
    private static final String SUB_COMMAND_ALIAS_2 = "subcommand2";
    private static final String COMMAND_ARG_1 = "commandArg1";
    private static final String SUB_COMMAND_ARG_1 = "subcommandArg1";
    private static final String COMMAND_ARG_1_KEY = "commandArg1Key";
    private static final String SUB_COMMAND_ARG_1_KEY = "subcommandArg1Key";

    @Mock
    private CommandArgumentsParser commandArgumentsParser;

    @InjectMocks
    private CommandResolver commandResolver;

    @Mock
    private TextChannel textChannel;

    @Mock
    private Command command;

    @BeforeEach
    void setUp()
    {
        openMocks(this);
        Mockito.reset(command, textChannel);
    }

    @Test
    void resolveAndParseCommandArgsShouldReturnCommandParsingChainWithCommandAndSubcommandAndArguments() throws Exception
    {
        // given
        String args = COMMAND_ARG_1 + " " + SUB_COMMAND_ALIAS_1 + " " + SUB_COMMAND_ARG_1;

        Command subcommand1 = prepareCommand(List.of(SUB_COMMAND_ALIAS_1));
        Command subcommand2 = prepareCommand(List.of(SUB_COMMAND_ALIAS_2));
        List<Command> subcommands = List.of(subcommand1, subcommand2);
        given(command.getSubCommands()).willReturn(subcommands);
        given(command.getParameters()).willReturn(Collections.singletonList(Parameter.builder().key(COMMAND_ARG_1_KEY).type(String.class).build()));
        given(subcommand1.getParameters()).willReturn(Collections.singletonList(Parameter.builder().key(SUB_COMMAND_ARG_1_KEY).type(String.class).build()));
        given(commandArgumentsParser.parseCommandArgs(textChannel, command, new ArrayDeque<>(Arrays.asList(COMMAND_ARG_1)))).willReturn(Map.of(COMMAND_ARG_1_KEY, COMMAND_ARG_1));
        given(commandArgumentsParser.parseCommandArgs(textChannel, subcommand1, new ArrayDeque<>(Arrays.asList(SUB_COMMAND_ARG_1)))).willReturn(Map.of(SUB_COMMAND_ARG_1_KEY, SUB_COMMAND_ARG_1));

        // when
        CommandParsingChain parsingChain = assertDoesNotThrow(() -> commandResolver.resolveAndParseCommandArgs(textChannel, command, args));

        // then
        assertThat(parsingChain.getCommandChain()).containsExactlyElementsOf(Arrays.asList(command, subcommand1));
        assertThat(parsingChain.getArguments()).containsAllEntriesOf(Map.of(COMMAND_ARG_1_KEY, COMMAND_ARG_1, SUB_COMMAND_ARG_1_KEY, SUB_COMMAND_ARG_1));
    }

    @Test
    void resolveAndParseCommandArgsShouldOnlyHaveCommandObjectIfCommandDoesNotHaveSubcommandsAndParameters()
    {
        // given
        String args = COMMAND_ARG_1 + " " + SUB_COMMAND_ALIAS_1 + " " + SUB_COMMAND_ARG_1;

        // when
        CommandParsingChain parsingChain = assertDoesNotThrow(() -> commandResolver.resolveAndParseCommandArgs(textChannel, command, args));

        // then
        assertThat(parsingChain.getCommandChain()).contains(command);
        assertThat(parsingChain.getArguments()).isEmpty();
    }

    @Test
    void resolveAndParseCommandArgsShouldThrowCommandArgumentsParseExceptionWhenArgumentCouldNotBeParsed() throws Exception
    {
        // given
        String args = COMMAND_ARG_1;
        given(commandArgumentsParser.parseCommandArgs(any(TextChannel.class), any(Command.class), any(Queue.class))).willThrow(new CommandArgumentsParseException(null));

        // when
        // then
         commandResolver.resolveAndParseCommandArgs(textChannel, command, args);
    }

    private Command prepareCommand(List<String> aliases)
    {
        Command command = mock(Command.class);
        given(command.getAliases()).willReturn(aliases);
        return command;
    }
}