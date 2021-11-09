package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.arguments.ArgumentType;

import java.util.List;

public interface CommandSpec
{
    Command getCommand();
//    ICommandArgument[] getArguments();
    ArgumentType[] getArguments();
    String getDescription();
    String getName();
    List<String> parseArguments();
}
