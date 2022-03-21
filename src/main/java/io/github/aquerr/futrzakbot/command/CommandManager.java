package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.context.CommandContextImpl;
import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.games.GameManager;
import io.github.aquerr.futrzakbot.games.QuoteGame;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandManager
{
    public static final String COMMAND_PREFIX = "!f";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private static final String ERROR_COMMAND_CORRECT_USAGE = "error.command.correct-usage";
    private static final String ERROR_PARSING_OF_COMMAND_PARAMETERS = "error.command.parameters.parsing";
    private static final String GENERAL_MESSAGE_LOG = "general.message.log";

    private final Map<List<String>, Command> commands = new LinkedHashMap<>();
    private final MessageSource messageSource;
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final GameManager gameManager;
    private final CommandParametersParsingManager commandArgumentParser = new CommandParametersParsingManager();

    public CommandManager(MessageSource messageSource,
                          FutrzakAudioPlayerManager futrzakAudioPlayerManager,
                          GameManager gameManager)
    {
        this.messageSource = messageSource;
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.gameManager = gameManager;
        initCommands();
    }

    private void initCommands()
    {
        addCommand(new HelpCommand(this, this.messageSource));
        addCommand(new EightBallCommand());
        addCommand(new RouletteCommand());
        addCommand(new DebilCommand());
        addCommand(new LoveCommand());
        addCommand(new FutrzakCommand(this.gameManager.getFutrzakGame()));
        addCommand(new PlayCommand(this.futrzakAudioPlayerManager));
        addCommand(new StopCommand(this.futrzakAudioPlayerManager));
        addCommand(new ResumeCommand(this.futrzakAudioPlayerManager));
        addCommand(new VolumeCommand(this.futrzakAudioPlayerManager));
        addCommand(new SkipCommand(this.futrzakAudioPlayerManager));
        addCommand(new ClearCommand(this.futrzakAudioPlayerManager));
        addCommand(new QueueCommand(this.futrzakAudioPlayerManager));
        addCommand(new InfoCommand(this.futrzakAudioPlayerManager));
        addCommand(new FightCommand());
        addCommand(new QuoteCommand(QuoteGame.getInstance()));
    }

    public void addCommand(Command command)
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
        String text = message.getContentRaw().substring(COMMAND_PREFIX.length() + 1); // Remove "!f "
        String commandAlias = text.split(" ")[0]; // Take command alias
        String arguments = text.substring(commandAlias.length()).trim(); // Rest are arguments

        Command command = getCommand(commandAlias).orElse(null);
        if(command == null)
            return;

        if(!hasPermissions(member, command))
            return;

        CommandContextImpl.CommandContextImplBuilder commandContextBuilder = CommandContextImpl.builder()
                .textChannel(channel)
                .member(member);

        try
        {
            commandContextBuilder = commandArgumentParser.parseCommandArguments(commandContextBuilder, command, arguments);
        }
        catch (CommandArgumentsParseException exception)
        {
            MessageEmbed messageEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .addField(messageSource.getMessage(ERROR_COMMAND_CORRECT_USAGE), command.getUsage(), false)
                    .build();
            channel.sendMessageEmbeds(messageEmbed).queue();
            LOGGER.error(messageSource.getMessage(ERROR_PARSING_OF_COMMAND_PARAMETERS, command.getName(), exception.getMessage()));
            return;
        }

        CommandContext commandContext = commandContextBuilder.build();

        //Log
        logCommandUsage(member, channel, message);

        //Execute
        command.execute(commandContext);
    }

    private void logCommandUsage(Member member, TextChannel channel, Message message)
    {
        LOGGER.info(messageSource.getMessage(GENERAL_MESSAGE_LOG, channel.getGuild().getName(),
                channel.getName(), member.getEffectiveName(),
                message.getContentDisplay()));
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
}
