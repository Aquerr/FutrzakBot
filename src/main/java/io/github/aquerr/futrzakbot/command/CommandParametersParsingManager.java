package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.command.context.CommandContextImpl;
import io.github.aquerr.futrzakbot.command.parsers.ArgumentParser;
import io.github.aquerr.futrzakbot.command.parsers.NumberArgumentParser;
import io.github.aquerr.futrzakbot.command.parsers.StringArgumentParser;

import java.util.*;

public class CommandParametersParsingManager
{
    private static final String DEFAULT_ARG_SEPARATOR = " ";

    private final Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();

    public CommandParametersParsingManager()
    {
        registerParsers();
    }

    private void registerParsers()
    {
        this.parsers.put(Integer.class, new NumberArgumentParser());
        this.parsers.put(String.class, new StringArgumentParser());
    }

    public CommandContextImpl.CommandContextImplBuilder parseCommandArguments(CommandContextImpl.CommandContextImplBuilder commandContextBuilder, Command command, String argsMessage) throws CommandArgumentsParseException
    {
        List<Parameter<?>> parameters = command.getParameters();
        if (parameters == null || parameters.size() == 0)
            return commandContextBuilder;

        String[] commandArgs = splitCommandArgs(argsMessage);
        Iterator<String> argsIterator = Arrays.stream(commandArgs).iterator();
        Iterator<Parameter<?>> argumentTypeIterator = parameters.iterator();

        while (argsIterator.hasNext())
        {
            String arg = argsIterator.next();
            Parameter<?> parameter = null;
            if (argumentTypeIterator.hasNext())
            {
                parameter = argumentTypeIterator.next();
            }

            if (parameter == null)
                throw new CommandArgumentsParseException("Not enough command parsers for given argument string!");

            if (parameter instanceof RemainingStringsParameter)
            {
                commandContextBuilder.put(parameter.getKey(), argsMessage.substring(argsMessage.indexOf(arg)));
                break;
            }
            ArgumentParser<?> parser = getParserForParameter(parameter);
            try
            {
                Object parsedArgument = parser.parse(arg);
                commandContextBuilder.put(parameter.getKey(), parsedArgument);
            }
            catch (Exception exception)
            {
                throw new CommandArgumentsParseException("Could not parse command argument: " + arg);
            }
        }

        return commandContextBuilder;
    }

    private ArgumentParser<?> getParserForParameter(Parameter<?> parameter)
    {
        Class<?> clazz = parameter.getType();
        return Optional.ofNullable(parsers.get(clazz))
                .orElseThrow(() -> new IllegalStateException("No parser for parameter type '" + parameter.getType() + "' has been found."));
    }

    private String[] splitCommandArgs(String argsMessage)
    {
        return argsMessage.split(DEFAULT_ARG_SEPARATOR);
    }
}
