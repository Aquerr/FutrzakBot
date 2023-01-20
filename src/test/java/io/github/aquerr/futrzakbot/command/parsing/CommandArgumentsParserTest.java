package io.github.aquerr.futrzakbot.command.parsing;

import io.github.aquerr.futrzakbot.discord.command.Command;
import io.github.aquerr.futrzakbot.discord.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.discord.command.parameters.IntegerParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.StringParameter;
import io.github.aquerr.futrzakbot.discord.command.parsing.CommandArgumentsParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.CommandParsingChain;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.ArgumentParser;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

class CommandArgumentsParserTest
{
    private static final String DEFAULT_ARG_SEPARATOR = " ";
    private static final String ARGUMENT_1 = "argument1";
    private static final Integer ARGUMENT_2 = 2;

    private static final String STRING_PARAMETER_KEY = "stringKey";
    private static final String INTEGER_PARAMETER_KEY = "integerKey";

    private static final String SUB_COMMAND_ALIAS_1 = "subcommand1";
    private static final String SUB_COMMAND_ALIAS_2 = "subcommand2";
    private static final String COMMAND_ARG_1 = "commandArg1";
    private static final String SUB_COMMAND_ARG_1 = "subcommandArg1";
    private static final String COMMAND_ARG_1_KEY = "commandArg1Key";
    private static final String SUB_COMMAND_ARG_1_KEY = "subcommandArg1Key";

    private Map<Class<?>, ArgumentParser<?>> parsers;

    private CommandArgumentsParser commandArgumentsParser;

    @Mock
    private TextChannel textChannel;
    @Mock
    private Command command;
    @Mock
    private ArgumentParser<String> stringArgumentParser;
    @Mock
    private ArgumentParser<Integer> integerArgumentParser;

    @BeforeEach
    void setUp()
    {
        openMocks(this);
        this.parsers = new HashMap<>();
        this.commandArgumentsParser = new CommandArgumentsParser(this.parsers);
        Mockito.reset(textChannel, command, stringArgumentParser, integerArgumentParser);
    }

    @Test
    void resolveAndParseCommandArgsShouldReturnCommandParsingChainWithCommandAndSubcommandAndArguments() throws Exception
    {
        // given
        this.parsers.put(String.class, this.stringArgumentParser);
        String args = prepareArgsMessage(COMMAND_ARG_1, SUB_COMMAND_ALIAS_1, SUB_COMMAND_ARG_1);
        Command subcommand1 = prepareCommand(List.of(SUB_COMMAND_ALIAS_1));
        Command subcommand2 = prepareCommand(List.of(SUB_COMMAND_ALIAS_2));
        List<Command> subcommands = List.of(subcommand1, subcommand2);
        given(command.getSubCommands()).willReturn(subcommands);
        given(command.getParameters()).willReturn(Collections.singletonList(StringParameter.builder().key(COMMAND_ARG_1_KEY).build()));
        given(subcommand1.getParameters()).willReturn(Collections.singletonList(StringParameter.builder().key(SUB_COMMAND_ARG_1_KEY).build()));
        given(stringArgumentParser.parse(any())).willReturn(COMMAND_ARG_1, SUB_COMMAND_ARG_1);

        // when
        CommandParsingChain parsingChain = assertDoesNotThrow(() -> commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, args));

        // then
        assertThat(parsingChain.getCommandChain()).containsExactlyElementsOf(Arrays.asList(command, subcommand1));
        assertThat(parsingChain.getArguments()).containsAllEntriesOf(Map.of(COMMAND_ARG_1_KEY, COMMAND_ARG_1, SUB_COMMAND_ARG_1_KEY, SUB_COMMAND_ARG_1));
    }

    @Test
    void resolveAndParseCommandArgsShouldOnlyHaveCommandObjectIfCommandDoesNotHaveSubcommandsAndParameters()
    {
        // given
        String args = prepareArgsMessage(COMMAND_ARG_1, SUB_COMMAND_ALIAS_1, SUB_COMMAND_ARG_1);

        // when
        CommandParsingChain parsingChain = assertDoesNotThrow(() -> commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, args));

        // then
        assertThat(parsingChain.getCommandChain()).contains(command);
        assertThat(parsingChain.getArguments()).isEmpty();
    }

    @Test
    void resolveAndParseCommandArgsShouldParseArgumentsWhenCommandHasParametersAndReturnMapWithParsedArguments() throws CommandArgumentsParseException, ArgumentParseException
    {
        // given
        List<String> args = Arrays.asList(ARGUMENT_1, ARGUMENT_2.toString());
        parsers.put(String.class, stringArgumentParser);
        parsers.put(Integer.class, integerArgumentParser);
        given(command.getParameters()).willReturn(Arrays.asList(
                StringParameter.builder().key(STRING_PARAMETER_KEY).build(),
                IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build(),
                IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).optional(true).build()));
        given(stringArgumentParser.parse(any())).willReturn(ARGUMENT_1);
        given(integerArgumentParser.parse(any())).willReturn(ARGUMENT_2);

        // when
        CommandParsingChain commandParsingChain = commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage(args));

        // then
        assertThat(commandParsingChain.getArguments()).containsAllEntriesOf(Map.of(STRING_PARAMETER_KEY, ARGUMENT_1, INTEGER_PARAMETER_KEY, ARGUMENT_2));
    }

    @Test
    void resolveAndParseCommandArgsShouldThrowCommandArgumentsParseExceptionWhenProvidedNotEnoughArguments()
    {
        // given
        given(command.getParameters()).willReturn(Collections.singletonList(StringParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        // then
        assertThrows(CommandArgumentsParseException.class, () -> commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage()));
    }

    @Test
    void resolveAndParseCommandArgsShouldPutAllArgsInOneKeyWhenEncountersRemainingStringsParameter() throws CommandArgumentsParseException
    {
        // given
        List<String> args = Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString());
        given(command.getParameters()).willReturn(Collections.singletonList(RemainingStringsParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        CommandParsingChain commandParsingChain = commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage(args));

        // then
        assertThat(commandParsingChain.getArguments()).containsExactlyEntriesOf(Map.of(STRING_PARAMETER_KEY, ARGUMENT_1 + " " + ARGUMENT_1 + " " + ARGUMENT_2));
    }

    @Test
    void resolveAndParseCommandArgsShouldNotAddAnySpacesToArgumentWhenEncountersRemainingStringsParameter() throws CommandArgumentsParseException
    {
        // given
        List<String> args = Arrays.asList(ARGUMENT_1);
        given(command.getParameters()).willReturn(Collections.singletonList(RemainingStringsParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        CommandParsingChain commandParsingChain = commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage(args));

        // then
        assertThat(commandParsingChain.getArguments()).containsAllEntriesOf(Map.of(STRING_PARAMETER_KEY, ARGUMENT_1));
    }

    @Test
    void resolveAndParseCommandArgsShouldNotParseMoreParametersWhenEncountersRemainingStringsParameter() throws CommandArgumentsParseException
    {
        // given
        List<String> args = Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString());
        given(command.getParameters()).willReturn(Arrays.asList(RemainingStringsParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));
        parsers.put(String.class, stringArgumentParser);

        // when
        CommandParsingChain commandParsingChain = commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage(args));

        // then
        assertThat(commandParsingChain.getArguments()).containsOnlyKeys(STRING_PARAMETER_KEY);
    }

    @Test
    void resolveAndParseCommandArgsShouldThrowIllegalStateExceptionWhenNoParserHasBeenFoundForGivenParameterType()
    {
        // given
        List<String> args = Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString());
        given(command.getParameters()).willReturn(Arrays.asList(StringParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));

        // when
        // then
        assertThrows(IllegalStateException.class, () -> commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage(args)));
    }

    @ValueSource(classes = {ArgumentParseException.class, IllegalArgumentException.class, NumberFormatException.class, NullPointerException.class})
    @ParameterizedTest
    void resolveAndParseCommandArgsShouldThrowCommandArgumentsParseExceptionWhenParserThrowsAnyException(Class<? extends Throwable> exceptionClass) throws ArgumentParseException
    {
        // given
        List<String> args = Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString());
        parsers.put(String.class, stringArgumentParser);
        given(command.getParameters()).willReturn(Arrays.asList(StringParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));
        given(stringArgumentParser.parse(any())).willThrow(exceptionClass);

        // when
        // then
        assertThrows(CommandArgumentsParseException.class, () -> commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage(args)));
    }

    @Test
    void resolveAndParseCommandArgsShouldContinueIfArgumentIsMissingAndParameterIsOptional()
    {
        // given
        parsers.put(String.class, stringArgumentParser);
        given(command.getParameters()).willReturn(List.of(StringParameter.builder().key(STRING_PARAMETER_KEY).optional(true).build()));

        // when
        // then
        assertDoesNotThrow(() -> commandArgumentsParser.resolveAndParseCommandArgs(textChannel, command, prepareArgsMessage()));
    }

    private String prepareArgsMessage(String... args)
    {
        return String.join(DEFAULT_ARG_SEPARATOR, args);
    }

    private String prepareArgsMessage(List<String> args)
    {
        return String.join(DEFAULT_ARG_SEPARATOR, args);
    }

    private Command prepareCommand(List<String> aliases)
    {
        Command command = mock(Command.class);
        given(command.getAliases()).willReturn(aliases);
        return command;
    }
}