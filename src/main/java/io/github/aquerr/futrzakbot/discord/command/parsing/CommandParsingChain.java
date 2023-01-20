package io.github.aquerr.futrzakbot.discord.command.parsing;

import io.github.aquerr.futrzakbot.discord.command.Command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CommandParsingChain
{
    LinkedList<Command> commandChain = new LinkedList<>();
    Map<String, Object> arguments = new HashMap<>();

    public CommandParsingChain appendCommand(Command command)
    {
        this.commandChain.add(command);
        return this;
    }

    public CommandParsingChain putArgument(String key, Object value)
    {
        this.arguments.put(key, value);
        return this;
    }

    public Map<String, Object> getArguments()
    {
        return arguments;
    }

    public LinkedList<Command> getCommandChain()
    {
        return commandChain;
    }
}
