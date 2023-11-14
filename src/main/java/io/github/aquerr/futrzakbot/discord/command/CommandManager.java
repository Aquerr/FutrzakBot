package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContextImpl;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.listener.SlashCommandListener;
import io.github.aquerr.futrzakbot.discord.command.listener.TextCommandListener;
import io.github.aquerr.futrzakbot.discord.command.parsing.CommandArgumentsParser;
import io.github.aquerr.futrzakbot.discord.command.parsing.CommandParsingChain;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager implements EventListener
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
    private final SlashCommandListener slashCommandListener;
    private final TextCommandListener textCommandListener;

    public CommandManager(MessageSource messageSource)
    {
        this.messageSource = messageSource;
        this.commandArgumentParser = CommandArgumentsParser.createDefault();
        this.slashCommandListener = new SlashCommandListener(this);
        this.textCommandListener = new TextCommandListener(this, FutrzakMessageEmbedFactory.getInstance(), messageSource);
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

    public void processTextCommand(Member member, MessageChannelUnion channel, Message message)
    {
        //Log
        logCommandUsage(member, channel, message);

        String messageContentRaw = message.getContentRaw();
        if (CommandManager.COMMAND_PREFIX.equals(messageContentRaw))
            messageContentRaw = CommandManager.COMMAND_PREFIX + getHelpCommandAlias();

        String text = messageContentRaw.substring(COMMAND_PREFIX.length() + 1); // Remove "!f "
        String commandAlias = text.split(" ")[0]; // Take command alias
        String arguments = text.substring(commandAlias.length()).trim(); // Rest are arguments

        Command command = getCommand(commandAlias).orElse(null);
        if(command == null)
            return;

        processTextCommand(member, channel, command, arguments);
    }

    private String getHelpCommandAlias()
    {
        return getCommands().values().stream()
                .filter(command -> command instanceof HelpCommand)
                .map(Command::getAliases)
                .map(aliases -> aliases.get(0))
                .findFirst()
                .orElse("help");
    }

    private void processTextCommand(Member member, MessageChannelUnion channel, Command command, String arguments)
    {
        if(!hasPermissions(member, command))
            return;

        CommandContextImpl.CommandContextImplBuilder commandContextBuilder = CommandContextImpl.builder()
                .messageChannelUnion(channel)
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

    private void logCommandUsage(Member member, MessageChannelUnion channel, Message message)
    {
        if (LOGGER.isInfoEnabled())
        {
            String guildName = channel.getType().isGuild() ? channel.asGuildMessageChannel().getGuild().getName() : null;
            LOGGER.info(messageSource.getMessage(GENERAL_MESSAGE_LOG, guildName,
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

    private void handleArgumentsParseException(MessageChannelUnion channel, Command command, CommandArgumentsParseException exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_PARSING_OF_COMMAND_PARAMETERS, command.getName(), exception.getMessage()));
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .addField(messageSource.getMessage(ERROR_COMMAND_CORRECT_USAGE), command.getUsage(), false)
                .build();
        channel.sendMessageEmbeds(messageEmbed).queue();
    }

    private void handleCommandException(MessageChannelUnion channel, Command command, CommandException exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_COMMAND_EXCEPTION, command.getName(), exception.getMessage()));
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(messageSource.getMessage(ERROR_COMMAND_EXCEPTION, exception.getMessage()))
                .build();
        channel.sendMessageEmbeds(messageEmbed).queue();
    }

    private void handleSlashCommandException(InteractionHook interactionHook, SlashCommand command, CommandException exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_COMMAND_EXCEPTION, command.getAliases().get(0), exception.getMessage()));
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(messageSource.getMessage(ERROR_COMMAND_EXCEPTION, exception.getMessage()))
                .build();
        interactionHook.editOriginalEmbeds(messageEmbed).queue();
    }

    private void handleException(MessageChannelUnion channel, Command command, Exception exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_GENERAL, command.getName(), exception.getMessage()), exception);
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(messageSource.getMessage(ERROR_GENERAL, exception.getMessage()))
                .build();
        channel.sendMessageEmbeds(messageEmbed).queue();
    }

    private void handleSlashException(InteractionHook interactionHook, SlashCommand command, Exception exception)
    {
        LOGGER.error(messageSource.getMessage(ERROR_GENERAL, command.getAliases().get(0), exception.getMessage()), exception);
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(messageSource.getMessage(ERROR_GENERAL, exception.getMessage()))
                .build();
        interactionHook.editOriginalEmbeds(messageEmbed).queue();
    }

    public void registerSlashCommandsForGuild(Guild guild)
    {
        List<CommandData> slashCommandData = new ArrayList<>();
        for (Command command : getCommands().values())
        {
            if (!(command instanceof SlashCommand slashCommand))
                continue;

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

    @Override
    public void onEvent(@NotNull GenericEvent event)
    {
        if (event instanceof GenericInteractionCreateEvent newEvent)
        {
            this.slashCommandListener.onEvent(newEvent);
        }
        else
        {
            this.textCommandListener.onEvent(event);
        }
    }

    public void processSlashCommand(SlashCommand slashCommand, SlashCommandInteractionEvent event)
    {
        InteractionHook interactionHook = event.getHook();
        try
        {
            slashCommand.onSlashCommand(event);
            completeEventIfNotAcknowledged(event);
        }
        catch (CommandException exception)
        {
            // Normal Command Exception handling here...
            completeEventIfNotAcknowledged(event);
            handleSlashCommandException(interactionHook, slashCommand, exception);
        }
        catch (Exception exception)
        {
            // General error...
            completeEventIfNotAcknowledged(event);
            handleSlashException(interactionHook, slashCommand, exception);
        }
    }

    public void processSlashButtons(SlashCommand slashCommand, ButtonInteractionEvent event)
    {
        InteractionHook interactionHook = event.getHook();
        try
        {
            slashCommand.onButtonClick(event);
            completeEventIfNotAcknowledged(event);
        }
        catch (CommandException exception)
        {
            // Normal Command Exception handling here...
            completeEventIfNotAcknowledged(event);
            handleSlashCommandException(interactionHook, slashCommand, exception);
        }
        catch (Exception exception)
        {
            // General error...
            completeEventIfNotAcknowledged(event);
            handleSlashException(interactionHook, slashCommand, exception);
        }
    }

    private void completeEventIfNotAcknowledged(GenericCommandInteractionEvent event)
    {
        try
        {
            if (!event.isAcknowledged())
            {
                event.deferReply().complete();
            }
        }
        catch (Exception exception)
        {
            // ignored.
        }
    }

    private void completeEventIfNotAcknowledged(GenericComponentInteractionCreateEvent event)
    {
        try
        {
            if (!event.isAcknowledged())
            {
                event.deferReply().complete();
            }
        }
        catch (Exception exception)
        {
            // ignored.
        }
    }
}