package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.arguments.ArgumentType;

import java.util.List;

public class CommandSpecImpl implements CommandSpec
{
    private final String name;
    private final String description;
    private final ArgumentType[] arguments;
    private final Command command;

    private CommandSpecImpl(String name, String description, ArgumentType[] arguments, Command command)
    {
        this.name = name;
        this.description = description;
        this.arguments = arguments;
        this.command = command;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    public Command getCommand()
    {
        return this.command;
    }

    @Override
    public ArgumentType[] getArguments()
    {
        return this.arguments;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<String> parseArguments()
    {
        return null;
    }

    public static class Builder
    {
        private String name;
        private String description;
        private ArgumentType[] arguments;
        private Command command;

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder description(String description)
        {
            this.description = description;
            return this;
        }

        public Builder arguments(ArgumentType... arguments)
        {
            this.arguments = arguments;
            return this;
        }

        public Builder command(Command command)
        {
            this.command = command;
            return this;
        }

        public CommandSpecImpl build()
        {
            if(name == null)
                throw new NullPointerException("Name cannot be null");

            if(command == null)
                throw new NullPointerException("Command cannot be null!");

            return new CommandSpecImpl(name, description, arguments, command);
        }
    }
}
