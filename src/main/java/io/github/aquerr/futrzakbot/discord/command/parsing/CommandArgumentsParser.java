package io.github.aquerr.futrzakbot.discord.command.parsing;

import io.github.aquerr.futrzakbot.discord.command.Command;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.ArgumentParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.DoubleArgumentParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.IntegerArgumentParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.MemberArgumentParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.StringArgumentParser;
import io.github.aquerr.futrzakbot.util.StringUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class CommandArgumentsParser
{
    private static final String DEFAULT_ARG_SEPARATOR = " ";

    private final Map<Class<?>, ArgumentParser<?>> parsers;

    public CommandArgumentsParser(Map<Class<?>, ArgumentParser<?>> parsers)
    {
        this.parsers = parsers;
    }

    public static CommandArgumentsParser createDefault()
    {
        return new CommandArgumentsParser(getDefaultParsers());
    }

    private static Map<Class<?>, ArgumentParser<?>> getDefaultParsers()
    {
        Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();
        parsers.put(Integer.class, new IntegerArgumentParser());
        parsers.put(String.class, new StringArgumentParser());
        parsers.put(Double.class, new DoubleArgumentParser());
        parsers.put(Member.class, new MemberArgumentParser());
        return parsers;
    }

    public CommandParsingChain resolveAndParseCommandArgs(TextChannel textChannel, Command command, String argsMessage) throws CommandArgumentsParseException
    {
        Queue<String> args = new ArrayDeque<>(StringUtils.isBlank(argsMessage) ? Collections.emptyList() : Arrays.asList(argsMessage.split(DEFAULT_ARG_SEPARATOR)));
        CommandParsingChain parsingChain = new CommandParsingChain();
        return resolveAndParseCommandArgs(parsingChain, textChannel, command, args);
    }

    private CommandParsingChain resolveAndParseCommandArgs(CommandParsingChain parsingChain, TextChannel textChannel, Command command, Queue<String> args) throws CommandArgumentsParseException
    {
        parsingChain.appendCommand(command);

        // Parse required command args
        parseCommandArguments(parsingChain,
                textChannel,
                command.getParameters().stream().filter(parameter -> !parameter.isOptional()).collect(Collectors.toList()),
                args);

        // Check subcommand. If it is subcommand then we skip optional arguments for this command.
        // It is up to the programmer to ensure correctness of subcommands aliases and optional parameters.
        Command subCommand = findUsedSubcommand(command, args);
        if (subCommand != null)
            return resolveAndParseCommandArgs(parsingChain, textChannel, subCommand, args);

        // Parse optional command args or subcommand
        parseCommandArguments(parsingChain,
                textChannel,
                command.getParameters().stream()
                .filter(Parameter::isOptional)
                .collect(Collectors.toList()),
                args);

        return parsingChain;
    }

    private Command findUsedSubcommand(Command command, Queue<String> args)
    {
        String subCommandAlias = args.poll();
        if (subCommandAlias == null)
            return null;

        for (final Command subCommand : command.getSubCommands())
        {
            if (subCommand.getAliases().contains(subCommandAlias))
            {
                return subCommand;
            }
        }
        return null;
    }

    private void parseCommandArguments(CommandParsingChain parsingChain, TextChannel textChannel, List<Parameter<?>> commandParameters, Queue<String> args) throws CommandArgumentsParseException
    {
        if (commandParameters.size() != 0)
        {
            Map<String, Object> parsedParameters = parseCommandArgs(textChannel, commandParameters, args);
            parsedParameters.forEach(parsingChain::putArgument);
        }
    }

    private Map<String, Object> parseCommandArgs(TextChannel textChannel, List<Parameter<?>> parameters, Queue<String> args) throws CommandArgumentsParseException
    {
        Map<String, Object> parsedArguments = new HashMap<>();
        Iterator<Parameter<?>> commandParametersIterator = parameters.iterator();
        while (commandParametersIterator.hasNext())
        {
            Parameter<?> parameter = commandParametersIterator.next();
            String arg = args.poll();

            if (arg == null || "".equals(arg))
            {
                if (parameter.isOptional())
                    continue;
                throw new CommandArgumentsParseException("Not enough arguments");
            }

            if (parameter instanceof RemainingStringsParameter)
            {
                StringBuilder argsMessage = new StringBuilder(arg);
                for (final String remainingArg : args)
                {
                    argsMessage.append(DEFAULT_ARG_SEPARATOR).append(remainingArg);
                }
                parsedArguments.put(parameter.getKey(), argsMessage.toString());
                break;
            }

            ArgumentParser<?> parser = getParserForParameter(parameter);
            try
            {
                ParsingContext parsingContext = new ParsingContextImpl(textChannel, arg);
                Object parsedArgument = parser.parse(parsingContext);
                parsedArguments.put(parameter.getKey(), parsedArgument);
            }
            catch (Exception exception)
            {
                throw new CommandArgumentsParseException("Could not parse command argument: " + arg);
            }
        }
        return parsedArguments;
    }

    private ArgumentParser<?> getParserForParameter(Parameter<?> parameter)
    {
        Class<?> clazz = parameter.getType();
        return Optional.ofNullable(parsers.get(clazz))
                .orElseThrow(() -> new IllegalStateException("No parser for parameter type '" + parameter.getType() + "' has been found."));
    }
}
