package io.github.aquerr.futrzakbot.command.parsing;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    }

    @Test
    void resolveAndParseCommandArgsShouldReturnCommandParsingChainWithCommandAndSubcommandAndArguments() throws Exception
    {
        // given
        String args = COMMAND_ARG_1 + " " + SUB_COMMAND_ALIAS_1 + " " + SUB_COMMAND_ARG_1;

        Command subcommand1 = prepareCommand(List.of(SUB_COMMAND_ALIAS_1));
        Command subcommand2 = prepareCommand(List.of(SUB_COMMAND_ALIAS_2));
        List<Command> subcommands = Arrays.asList(subcommand1, subcommand2);
        given(command.getSubCommands()).willReturn(subcommands);
        given(command.getParameters()).willReturn(Collections.singletonList(Parameter.builder().key(COMMAND_ARG_1_KEY).type(String.class).build()));
        given(subcommand1.getParameters()).willReturn(Collections.singletonList(Parameter.builder().key(SUB_COMMAND_ARG_1_KEY).type(String.class).build()));
        given(commandArgumentsParser.parseCommandArgs(textChannel, command, new ArrayDeque<>(Arrays.asList(COMMAND_ARG_1)))).willReturn(Map.of(COMMAND_ARG_1_KEY, COMMAND_ARG_1));
        given(commandArgumentsParser.parseCommandArgs(textChannel, subcommand1, new ArrayDeque<>(Arrays.asList(SUB_COMMAND_ARG_1)))).willReturn(Map.of(SUB_COMMAND_ARG_1_KEY, SUB_COMMAND_ARG_1));

        // when
        CommandParsingChain parsingChain = assertDoesNotThrow(() -> commandResolver.resolveAndParseCommandArgs(textChannel, command, args));

        // then
        assertThat(parsingChain.getCommandChain()).containsExactlyElementsOf(Arrays.asList(command, subcommand1));
        assertThat(parsingChain.getArguments()).containsExactlyEntriesOf(Map.of(COMMAND_ARG_1_KEY, COMMAND_ARG_1, SUB_COMMAND_ARG_1_KEY, SUB_COMMAND_ARG_1));
    }

    private Command prepareCommand(List<String> aliases)
    {
        Command command = mock(Command.class);
        given(command.getAliases()).willReturn(aliases);
        return command;
    }
}