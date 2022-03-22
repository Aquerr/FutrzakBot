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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

public class CommandArgumentsParsingManager
{
    private static final String DEFAULT_ARG_SEPARATOR = " ";

    private final Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();

    public CommandArgumentsParsingManager()
    {
        registerParsers();
    }

    private void registerParsers()
    {
        this.parsers.put(Integer.class, new IntegerArgumentParser());
        this.parsers.put(String.class, new StringArgumentParser());
        this.parsers.put(Double.class, new DoubleArgumentParser());
        this.parsers.put(Member.class, new MemberArgumentParser());
    }

    public Map<String, Object> parseCommandArguments(TextChannel textChannel, Command command, String argsMessage) throws CommandArgumentsParseException
    {
        List<Parameter<?>> parameters = command.getParameters();
        if (parameters == null || parameters.size() == 0)
            return Collections.emptyMap();

        Map<String, Object> parsedParameters = new HashMap<>();
        StringTokenizer argumentTokenizer = new StringTokenizer(argsMessage, DEFAULT_ARG_SEPARATOR, false);
        Iterator<Parameter<?>> argumentTypeIterator = parameters.iterator();

        while (argumentTokenizer.hasMoreTokens())
        {
            String arg = argumentTokenizer.nextToken();
            Parameter<?> parameter = null;
            if (argumentTypeIterator.hasNext())
            {
                parameter = argumentTypeIterator.next();
            }

            if (parameter == null)
                throw new CommandArgumentsParseException("Not enough command parsers for given argument string!");

            if (parameter instanceof RemainingStringsParameter)
            {
                parsedParameters.put(parameter.getKey(), argsMessage.substring(argsMessage.indexOf(arg)));
                break;
            }
            ArgumentParser<?> parser = getParserForParameter(parameter);
            try
            {
                ParsingContext parsingContext = new ParsingContextImpl(textChannel, arg);
                Object parsedArgument = parser.parse(parsingContext);
                parsedParameters.put(parameter.getKey(), parsedArgument);
            }
            catch (Exception exception)
            {
                throw new CommandArgumentsParseException("Could not parse command argument: " + arg);
            }
        }

        return parsedParameters;
    }

    private ArgumentParser<?> getParserForParameter(Parameter<?> parameter)
    {
        Class<?> clazz = parameter.getType();
        return Optional.ofNullable(parsers.get(clazz))
                .orElseThrow(() -> new IllegalStateException("No parser for parameter type '" + parameter.getType() + "' has been found."));
    }
}
