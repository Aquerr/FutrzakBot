package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.context.CommandContextImpl;
import io.github.aquerr.futrzakbot.command.exception.CommandArgumentsParseException;
import io.github.aquerr.futrzakbot.games.QuoteGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CommandManager
{
    public static final String COMMAND_PREFIX = "!t";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private final Map<List<String>, Command> commands = new LinkedHashMap<>();
    private final FutrzakBot futrzakBot;
    private final CommandParametersParsingManager commandArgumentParser = new CommandParametersParsingManager();

    public CommandManager(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        initCommands();
    }

    private void initCommands()
    {
        addCommand(new HelpCommand(this));
        addCommand(new EightBallCommand());
        addCommand(new RouletteCommand());
        addCommand(new DebilCommand());
        addCommand(new LoveCommand());
        addCommand(new FutrzakCommand(this.futrzakBot.getGameManager().getFutrzakGame()));
        addCommand(new PlayCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new StopCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new ResumeCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new VolumeCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new SkipCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new RemoveComand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new ClearCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new QueueCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
        addCommand(new InfoCommand(this.futrzakBot.getFutrzakAudioPlayerManager()));
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
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField(":warning: Poprawne użycie komendy: ", command.getUsage(),false);
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
            LOGGER.error("Error during parsing arguments for command: {}, Exception message: {}", command.getName(), exception.getMessage());
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
        LOGGER.info("Guild: {}, Channel: {}, Member: {}, Message: {}", channel.getGuild().getName(),
                channel.getName(), member.getEffectiveName(),
                message.getContentDisplay());
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
