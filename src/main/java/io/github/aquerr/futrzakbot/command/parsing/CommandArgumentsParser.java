package io.github.aquerr.futrzakbot.command.parsing;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.command.parsing.parsers.ArgumentParser;
import io.github.aquerr.futrzakbot.command.parsing.parsers.DoubleArgumentParser;
import io.github.aquerr.futrzakbot.command.parsing.parsers.IntegerArgumentParser;
import io.github.aquerr.futrzakbot.command.parsing.parsers.MemberArgumentParser;
import io.github.aquerr.futrzakbot.command.parsing.parsers.StringArgumentParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class CommandArgumentsParser
{
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

    public Map<String, Object> parseCommandArgs(TextChannel textChannel, Command command, Queue<String> args) throws CommandArgumentsParseException
    {
        Map<String, Object> parsedArguments = new HashMap<>();
        Iterator<Parameter<?>> commandParametersIterator = command.getParameters().iterator();
        while (commandParametersIterator.hasNext())
        {
            Parameter<?> parameter = commandParametersIterator.next();
            String arg = args.poll();

            if (arg == null)
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
                    argsMessage.append(" ").append(remainingArg);
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
