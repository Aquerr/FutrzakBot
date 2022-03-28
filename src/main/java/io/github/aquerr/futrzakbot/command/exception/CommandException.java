package io.github.aquerr.futrzakbot.command.exception;

/**
 * Represents an exception that can be thrown by commands during execution.
 *
 * CommandExceptions are captured by CommandManager and shown in the console and in the channel where corresponding command was executed.
 */
public class CommandException extends Exception
{
    public CommandException()
    {

    }

    public CommandException(String message)
    {
        super(message);
    }

    public CommandException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CommandException(Throwable cause)
    {
        super(cause);
    }
}
