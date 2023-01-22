package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContextImpl;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.parsing.CommandArgumentsParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.CommandParsingChain;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager
{
    public static final String COMMAND_PREFIX = "!f";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private static final String ERROR_COMMAND_CORRECT_USAGE = "error.command.correct-usage";
    private static final String ERROR_PARSING_OF_COMMAND_PARAMETERS = "error.command.parameters.parsing";
    private static final String ERROR_COMMAND_EXCEPTION = "error.command.exception";
    private static final String ERROR_GENERAL = "error.command.general";
    private static final String GENERAL_MESSAGE_LOG = "general.message.log";

    private final Map<List<String>, Command> commands = new LinkedHashMap<>();
    private final MessageSource messageSource;
    private final CommandArgumentsParser commandArgumentParser;

    public CommandManager(MessageSource messageSource)
    {
        this.messageSource = messageSource;
        this.commandArgumentParser = CommandArgumentsParser.createDefault();
    }

    public CommandManager(MessageSource messageSource, CommandArgumentsParser commandArgumentParser)
    {
        this.messageSource = messageSource;
        this.commandArgumentParser = commandArgumentParser;
    }

    public void registerCommand(Command command)
    {
        if (command.getAliases().isEmpty())
            throw new IllegalArgumentException("Command must provide at least one alias! Command: " + command.getName());

        if(this.commands.containsKey(command.getAliases()))
            throw new IllegalArgumentException("CommandManager already contains a command with given alias: " + command.getAliases());

        this.commands.put(command.getAliases(), command);
    }

    public boolean hasPermissions(Member member, Command command)
    {
//        if(!command.getClass().isAnnotationPresent(BotCommand.class))
//            return true;

        //TODO: Add config for specifying the role that

        return true;
    }

    public Map<List<String>, Command> getCommands()
    {
        return this.commands;
    }

    public void processCommand(Member member, TextChannel channel, Message message)
    {
        //Log
        logCommandUsage(member, channel, message);

        String messageContentRaw = message.getContentRaw();
        if ("!f".equals(messageContentRaw))
            messageContentRaw = "!f help"; //TODO: What if help command alias change? Need to find better solution...

        String text = messageContentRaw.substring(COMMAND_PREFIX.length() + 1); // Remove "!f "
        String commandAlias = text.split(" ")[0]; // Take command alias
        String arguments = text.substring(commandAlias.length()).trim(); // Rest are arguments

        Command command = getCommand(commandAlias).orElse(null);
        if(command == null)
            return;

        processCommand(member, channel, command, arguments);
    }

    private void processCommand(Member member, TextChannel channel, Command command, String arguments)
    {
        if(!hasPermissions(member, command))
            return;

        CommandContextImpl.CommandContextImplBuilder commandContextBuilder = CommandContextImpl.builder()
                .textChannel(channel)
                .member(member);

        try
        {
            CommandParsingChain parsingChain = commandArgumentParser.resolveAndParseCommandArgs(channel, command, arguments);
            commandContextBuilder.putAll(parsingChain.getArguments());
            command = parsingChain.getCommandChain().getLast(); // Returns this command or last subcommand in chain
        }
        catch (CommandArgumentsParseException exception)
        {
            handleArgumentsParseException(channel, command, exception);
            return;
        }

        CommandContext commandContext = commandContextBuilder.build();

        //Execute
        try
        {
            command.execute(commandContext);
        }
        catch (CommandException exception)
        {
            // Normal Command Exception handling here...
            handleCommandException(channel, command, exception);
        }
        catch (Exception exception)
        {
            // General error...
            handleException(channel, command, exception);
        }
    }

    private void logCommandUsage(Member member, TextChannel channel, Message message)
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info(messageSource.getMessage(GENERAL_MESSAGE_LOG, channel.getGuild().getName(),
                    channel.getName(), member.getEffectiveName(),
                    message.getContentDisplay()));
        }
    }

    private Optional<Command> getCommand(String commandAlias)
    {
        for (List<String> commandAliases : this.commands.keySet())
        {
            if (commandAliases.contains(commandAlias))
                return Optional.of(this.commands.get(commandAliases));
        }
        return Optional.empty();
    }

    private void handleArgumentsParseException(TextChannel channel, Command command, CommandArgumentsParseException exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_PARSING_OF_COMMAND_PARAMETERS, command.getName(), exception.getMessage()));
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .addField(messageSource.getMessage(ERROR_COMMAND_CORRECT_USAGE), command.getUsage(), false)
                .build();
        channel.sendMessageEmbeds(messageEmbed).queue();
    }

    private void handleCommandException(TextChannel channel, Command command, CommandException exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_COMMAND_EXCEPTION, command.getName(), exception.getMessage()));
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(messageSource.getMessage(ERROR_COMMAND_EXCEPTION, exception.getMessage()))
                .build();
        channel.sendMessageEmbeds(messageEmbed).queue();
    }

    private void handleException(TextChannel channel, Command command, Exception exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_GENERAL, command.getName(), exception.getMessage()), exception);
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(messageSource.getMessage(ERROR_GENERAL, exception.getMessage()))
                .build();
        channel.sendMessageEmbeds(messageEmbed).queue();
    }

    public void registerSlashCommandsForGuild(Guild guild)
    {
        List<CommandData> slashCommandData = new ArrayList<>();
        for (Command command : getCommands().values())
        {
            if (!(command instanceof SlashCommand))
                continue;

            SlashCommand slashCommand = (SlashCommand) command;

            slashCommandData.add(slashCommand.getSlashCommandData());
        }
        CommandListUpdateAction updateAction = guild.updateCommands();
        updateAction.addCommands(slashCommandData).complete();
    }

    public List<SlashCommand> getSlashCommands()
    {
        return this.commands.values().stream()
                .filter(SlashCommand.class::isInstance)
                .map(SlashCommand.class::cast)
                .collect(Collectors.toList());
    }
}