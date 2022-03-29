package io.github.aquerr.futrzakbot.command.parsing;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CommandResolver
{
    private static final String DEFAULT_ARG_SEPARATOR = " ";

    private final CommandArgumentsParser commandArgumentsParser;

    public CommandResolver()
    {
        this.commandArgumentsParser = CommandArgumentsParser.createDefault();
    }

    public CommandParsingChain resolveAndParseCommandArgs(TextChannel textChannel, Command command, String argsMessage) throws CommandArgumentsParseException
    {
        Queue<String> args = new ArrayDeque<>(Arrays.asList(argsMessage.split(DEFAULT_ARG_SEPARATOR)));
        CommandParsingChain parsingChain = new CommandParsingChain();
        return resolveAndParseCommandArgs(parsingChain, textChannel, command, args);
    }

    private CommandParsingChain resolveAndParseCommandArgs(CommandParsingChain parsingChain, TextChannel textChannel, Command command, Queue<String> args) throws CommandArgumentsParseException
    {
        parsingChain.appendCommand(command);

        // Check command parameters
        tryParseCommandArguments(parsingChain, textChannel, command, args);

        // Check subcommands
        return tryProcessSubcommands(parsingChain, textChannel, command, args);
    }

    private CommandParsingChain tryProcessSubcommands(CommandParsingChain parsingChain, TextChannel textChannel, Command command, Queue<String> args) throws CommandArgumentsParseException
    {
        String subCommandAlias = args.poll();
        if (subCommandAlias == null)
            return parsingChain;

        for (final Command subCommand : command.getSubCommands())
        {
            if (subCommand.getAliases().contains(subCommandAlias))
            {
                return resolveAndParseCommandArgs(parsingChain, textChannel, subCommand, args);
            }
        }
        return parsingChain;
    }

    private void tryParseCommandArguments(CommandParsingChain parsingChain, TextChannel textChannel, Command command, Queue<String> args) throws CommandArgumentsParseException
    {
        List<Parameter<?>> commandParameters = command.getParameters();
        if (commandParameters != null && commandParameters.size() != 0)
        {
            Map<String, Object> parsedParameters = this.commandArgumentsParser.parseCommandArgs(textChannel, command, args);
            parsedParameters.forEach(parsingChain::putArgument);
        }
    }
}
