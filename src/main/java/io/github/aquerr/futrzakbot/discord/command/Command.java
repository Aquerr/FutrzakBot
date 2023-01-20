package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;

import java.util.Collections;
import java.util.List;

/**
 * Basic Command Interface.
 *
 * All commands need to implement this interface.
 */
public interface Command
{
    /**
     * Executes the command with given CommandContext.
     * @param commandContext for the command. Contains information about channel and member who invoked the command.
     * @return true if command did execute successfully or false if not
     * @throws CommandException if execution of the command fails.
     */
    boolean execute(CommandContext commandContext) throws CommandException;

    /**
     * Aliases of the command.
     *
     * Implementations should provide aliases which can be used to execute command.
     * @return the list of command aliases
     */
    List<String> getAliases();

    /**
     * Help information on how to use command. Default implementation returns: bot prefix + comma-separated aliases + parameters.
     * @return the usage help text.
     */
    default String getUsage()
    {
        StringBuilder usageBuilder = new StringBuilder();
        usageBuilder.append(CommandManager.COMMAND_PREFIX)
                .append(" ")
                .append(String.join(",", getAliases()))
                .append(" ");

        List<Parameter<?>> parameters = getParameters();

        if (!parameters.isEmpty())
        {
            for (final Parameter<?> parameter : parameters)
            {
                if (parameter.isOptional())
                    usageBuilder.append("[");

                usageBuilder.append("<")
                        .append(parameter.getKey())
                        .append(">");

                if (parameter.isOptional())
                    usageBuilder.append("]");

                usageBuilder.append(" ");
            }
        }
        return usageBuilder.toString();
    }

    /**
     * The name of the command.
     *
     * @return the name of the command.
     */
    String getName();

    /**
     * The description of the command. Mostly used in HelpCommand.
     *
     * @return the description of the command.
     */
    String getDescription();

    /**
     * Command parameters if any. Default implementation returns an empty list.
     *
     * @return the list of parameters that this command accepts.
     */
    default List<Parameter<?>> getParameters()
    {
        return Collections.emptyList();
    }

    /**
     * The subcommands of the command. There is no limit for subcommands a command can have.
     *
     * Subcommand can have its own subcommands etc.
     *
     * @return the {@link List<Command>} that contains subcommands for the given command.
     */
    default List<Command> getSubCommands()
    {
        return Collections.emptyList();
    }
}
