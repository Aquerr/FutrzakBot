package io.github.aquerr.futrzakbot.command.parsing;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.command.parameters.IntegerParameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.command.parameters.StringParameter;
import io.github.aquerr.futrzakbot.command.parsing.parsers.ArgumentParser;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

class CommandArgumentsParserTest
{
    private static final String ARGUMENT_1 = "argument1";
    private static final Integer ARGUMENT_2 = 2;

    private static final String STRING_PARAMETER_KEY = "stringKey";
    private static final String INTEGER_PARAMETER_KEY = "integerKey";

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
    void parseCommandArgsShouldParseArgumentsWhenCommandHasParametersAndReturnMapWithParsedArguments() throws CommandArgumentsParseException, ArgumentParseException
    {
        // given
        Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGUMENT_1, ARGUMENT_2.toString()));
        parsers.put(String.class, stringArgumentParser);
        parsers.put(Integer.class, integerArgumentParser);
        given(command.getParameters()).willReturn(Arrays.asList(StringParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));
        given(stringArgumentParser.parse(any())).willReturn(ARGUMENT_1);
        given(integerArgumentParser.parse(any())).willReturn(ARGUMENT_2);

        // when
        Map<String, Object> parsedArguments = commandArgumentsParser.parseCommandArgs(textChannel, command, args);

        // then
        assertThat(parsedArguments).containsAllEntriesOf(Map.of(STRING_PARAMETER_KEY, ARGUMENT_1, INTEGER_PARAMETER_KEY, ARGUMENT_2));
    }

    @Test
    void parseCommandArgsShouldThrowCommandArgumentsParseExceptionWhenNumberOfParametersIsMoreThanArguments()
    {
        // given
        Queue<String> args = new ArrayDeque<>();
        given(command.getParameters()).willReturn(Collections.singletonList(StringParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        // then
        assertThrows(CommandArgumentsParseException.class, () -> commandArgumentsParser.parseCommandArgs(textChannel, command, args));
    }

    @Test
    void parseCommandArgsShouldPutAllArgsInOneKeyWhenEncountersRemainingStringsParameter() throws CommandArgumentsParseException
    {
        // given
        Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString()));
        given(command.getParameters()).willReturn(Collections.singletonList(RemainingStringsParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        Map<String, Object> parsedArguments = commandArgumentsParser.parseCommandArgs(textChannel, command, args);

        // then
        assertThat(parsedArguments).containsExactlyEntriesOf(Map.of(STRING_PARAMETER_KEY, ARGUMENT_1 + " " + ARGUMENT_1 + " " + ARGUMENT_2));
    }

    @Test
    void parseCommandArgsShouldNotAddAnySpacesToArgumentWhenEncountersRemainingStringsParameter() throws CommandArgumentsParseException
    {
        // given
        Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGUMENT_1));
        given(command.getParameters()).willReturn(Collections.singletonList(RemainingStringsParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        Map<String, Object> parsedArguments = commandArgumentsParser.parseCommandArgs(textChannel, command, args);

        // then
        assertThat(parsedArguments).containsAllEntriesOf(Map.of(STRING_PARAMETER_KEY, ARGUMENT_1));
    }

    @Test
    void parseCommandArgsShouldNotParseMoreParametersWhenEncountersRemainingStringsParameter() throws CommandArgumentsParseException
    {
        // given
        Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString()));
        given(command.getParameters()).willReturn(Arrays.asList(RemainingStringsParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));
        parsers.put(String.class, stringArgumentParser);

        // when
        Map<String, Object> parsedArguments = commandArgumentsParser.parseCommandArgs(textChannel, command, args);

        // then
        assertThat(parsedArguments).containsOnlyKeys(STRING_PARAMETER_KEY);
    }

    @Test
    void parseCommandArgsShouldThrowIllegalStateExceptionWhenNoParserHasBeenFoundForGivenParameterType()
    {
        // given
        Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString()));
        given(command.getParameters()).willReturn(Arrays.asList(StringParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));

        // when
        // then
        assertThrows(IllegalStateException.class, () -> commandArgumentsParser.parseCommandArgs(textChannel, command, args));
    }

    @ValueSource(classes = {ArgumentParseException.class, IllegalArgumentException.class, NumberFormatException.class, NullPointerException.class})
    @ParameterizedTest
    void parseCommandArgsShouldThrowCommandArgumentsParseExceptionWhenParserThrowsAnyException(Class<? extends Throwable> exceptionClass) throws ArgumentParseException
    {
        // given
        Queue<String> args = new ArrayDeque<>(Arrays.asList(ARGUMENT_1, ARGUMENT_1, ARGUMENT_2.toString()));
        parsers.put(String.class, stringArgumentParser);
        given(command.getParameters()).willReturn(Arrays.asList(StringParameter.builder().key(STRING_PARAMETER_KEY).build(), IntegerParameter.builder().key(INTEGER_PARAMETER_KEY).build()));
        given(stringArgumentParser.parse(any())).willThrow(exceptionClass);

        // when
        // then
        assertThrows(CommandArgumentsParseException.class, () -> commandArgumentsParser.parseCommandArgs(textChannel, command, args));
    }

    @Test
    void parseCommandArgsShouldThrowCommandArgumentsParseExceptionWhenArgumentIsMissingAndParameterIsNotOptional()
    {
        // given
        Queue<String> args = new ArrayDeque<>(Collections.emptyList());
        parsers.put(String.class, stringArgumentParser);
        given(command.getParameters()).willReturn(List.of(StringParameter.builder().key(STRING_PARAMETER_KEY).build()));

        // when
        // then
        assertThrows(CommandArgumentsParseException.class, () -> commandArgumentsParser.parseCommandArgs(textChannel, command, args));
    }

    @Test
    void parseCommandArgsShouldContinueIfArgumentIsMissingAndParameterIsOptional()
    {
        // given
        Queue<String> args = new ArrayDeque<>(Collections.emptyList());
        parsers.put(String.class, stringArgumentParser);
        given(command.getParameters()).willReturn(List.of(StringParameter.builder().key(STRING_PARAMETER_KEY).optional(true).build()));

        // when
        // then
        assertDoesNotThrow(() -> commandArgumentsParser.parseCommandArgs(textChannel, command, args));
    }
}