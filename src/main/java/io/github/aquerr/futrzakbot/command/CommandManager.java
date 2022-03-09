package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import io.github.aquerr.futrzakbot.command.arguments.ArgumentType;
import io.github.aquerr.futrzakbot.command.parsers.NumberArgumentParser;
import io.github.aquerr.futrzakbot.command.parsers.QuotationsArgumentParser;
import io.github.aquerr.futrzakbot.command.parsers.StringArgumentParser;
import io.github.aquerr.futrzakbot.games.QuoteGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private final Map<List<String>, CommandSpec> commands = new HashMap<>();
    private final FutrzakBot futrzakBot;

    public CommandManager(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        initCommands();
    }

    private void initCommands()
    {
        addCommand(Collections.singletonList("help"), CommandSpecImpl.builder()
                .name("help")
                .description("Pokazuje wszystkie dostępne komendy")
                .command(new HelpCommand(this))
                .build());

        addCommand(Collections.singletonList("8ball"), CommandSpecImpl.builder()
                .name("8ball")
                .description("Wyrocznia odpowie na Twoje pytanie")
                .command(new EightBallCommand())
                .arguments(ArgumentType.REMINDED_STRINGS)
                .build());

        addCommand(Collections.singletonList("ruletka"), CommandSpecImpl.builder()
                .name("ruletka")
                .description("Rosyjska ruletka")
                .command(new RouletteCommand())
                .build());

        addCommand(Collections.singletonList("debil"), CommandSpecImpl.builder()
                .name("debil")
                .description("Debil?")
                .command(new DebilCommand())
                .arguments(ArgumentType.REMINDED_STRINGS)
                .build());

        addCommand(Collections.singletonList("love"), CommandSpecImpl.builder()
                .name("love")
                .description("Licznik miłości")
                .command(new LoveCommand())
                .arguments(ArgumentType.REMINDED_STRINGS)
                .build());

        addCommand(Collections.singletonList("futrzak"), CommandSpecImpl.builder()
                .name("futrzak")
                .description("Sprawdź status swojego futrzaka")
                .command(new FutrzakCommand(this.futrzakBot.getGameManager().getFutrzakGame()))
                .build());

        addCommand(Collections.singletonList("play"), CommandSpecImpl.builder()
                .name("play")
                .description("Dodaj podany utwór do kolejki odtwarzacza")
                .command(new PlayCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .arguments(ArgumentType.REMINDED_STRINGS)
                .build());

        addCommand(Collections.singletonList("stop"), CommandSpecImpl.builder()
                .name("stop")
                .description("Zatrzymaj odtwarzacz muzyki")
                .command(new StopCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .build());

        addCommand(Collections.singletonList("resume"), CommandSpecImpl.builder()
                .name("resume")
                .description("Wznów odtwarzacz")
                .command(new ResumeCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .build());

        addCommand(Collections.singletonList("volume"), CommandSpecImpl.builder()
                .name("volume")
                .description("Zmień głośność odtwarzacza")
                .command(new VolumeCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .arguments(ArgumentType.NUMBER)
                .build());

        addCommand(Collections.singletonList("skip"), CommandSpecImpl.builder()
                .name("skip")
                .description("Pomiń utwór")
                .command(new SkipCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .build());

        addCommand(Collections.singletonList("queue"), CommandSpecImpl.builder()
                .name("queue")
                .description("Sprawdź kolejkę utworów w odtwarzaczu")
                .command(new QueueCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .build());

        addCommand(Collections.singletonList("info"), CommandSpecImpl.builder()
                .name("info")
                .description("Sprawdź obecnie grający utwór")
                .command(new InfoCommand(this.futrzakBot.getFutrzakAudioPlayerManager()))
                .build());

        addCommand(Collections.singletonList("fight"), CommandSpecImpl.builder()
                .name("fight")
                .description("Walcz z innym futrzakiem")
                .command(new FightCommand())
                .arguments(ArgumentType.REMINDED_STRINGS)
                .build());

        addCommand(Collections.singletonList("quote"), CommandSpecImpl.builder()
                .name("quote")
                .description("Wylosuj cytat")
                .command(new QuoteCommand(QuoteGame.getInstance()))
                .build());
    }

    public void addCommand(List<String> aliases, CommandSpec commandSpec)
    {
        if(this.commands.containsKey(aliases))
            throw new IllegalArgumentException("CommandManager already contains a command with given alias: " + aliases);

        this.commands.put(aliases, commandSpec);
    }

    public Optional<Command> getCommand(String alias)
    {
        for (List<String> commandAliases : this.commands.keySet())
        {
            if (commandAliases.contains(alias))
                return Optional.of(this.commands.get(commandAliases).getCommand());
        }
        return Optional.empty();
    }

    public boolean executeCommand(String commandAlias, Member member, TextChannel channel, List<String> args)
    {
        for (List<String> commandAliases : this.commands.keySet())
        {
            if (commandAliases.contains(commandAlias))
                return this.commands.get(commandAliases).getCommand().execute(member, channel, args);
        }
        return false;
    }

    public boolean hasPermissions(Member member, Command command)
    {
        if(!command.getClass().isAnnotationPresent(BotCommand.class))
            return true;

        //TODO: Add config for specifying the role that

        return true;
    }

    private List<String> parseCommandArguments(CommandSpec commandSpec, String rowArgs)
    {
        List<String> parsedArguments = new ArrayList<>();
        ArgumentType[] argumentTypes = commandSpec.getArguments();
        if(argumentTypes == null)
            return Collections.emptyList();
        StringBuilder remindingArgs = new StringBuilder();
        remindingArgs.append(rowArgs);

        for(ArgumentType argumentType : argumentTypes)
        {
            if(remindingArgs.charAt(0) == ' ')
                remindingArgs.deleteCharAt(0);

            switch(argumentType)
            {
                case QUOTED_STRING:
                {
                    String parsedArgument = QuotationsArgumentParser.parse(remindingArgs);
                    if(parsedArgument == null)
                        return null;

                    parsedArguments.add(parsedArgument);
                    break;
                }
                case STRING:
                {
                    String parsedArgument = StringArgumentParser.parse(remindingArgs);
                    parsedArguments.add(parsedArgument);
                    break;
                }
                case REMINDED_STRINGS:
                {
                    String parsedArgument = remindingArgs.toString();
                    remindingArgs.setLength(0);

                    parsedArguments.add(parsedArgument);
                    break;
                }
                case NUMBER:
                {
                    String parsedArgument = NumberArgumentParser.parse(remindingArgs);
                    parsedArguments.add(parsedArgument);
                    break;
                }
            }
        }

        return parsedArguments;
    }

    public Map<List<String>, CommandSpec> getCommands()
    {
        return this.commands;
    }

    public void processCommand(Member member, TextChannel channel, Message message)
    {
        //Create a new class "parser" for commandManager that will take care of arguments.
        //Arguments can be different for each command. E.g. not every command needs to take arguments inside " "

        String text = message.getContentRaw().substring(3); // Remove "!f "
        String commandAlias = text.split(" ")[0]; // Take command alias
        String arguments = text.substring(commandAlias.length()).trim(); // Rest are arguments

//        Optional<ICommand> optionalCommand = getCommand(commandAlias);
//        if (!optionalCommand.isPresent())
//            return;

        Optional<CommandSpec> optionalCommandSpec = getCommandSpec(commandAlias);
        if(optionalCommandSpec.isEmpty())
            return;

        if(!hasPermissions(member, optionalCommandSpec.get().getCommand()))
            return;

        List<String> argsList = parseCommandArguments(optionalCommandSpec.get(), arguments);
        if(argsList == null)
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField(":warning: Poprawne użycie komendy: ", optionalCommandSpec.get().getCommand().getUsage(),false);
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
            LOGGER.info("Error during parsing arguments for command: " + optionalCommandSpec.get().getName());
            LOGGER.info("Used arguments: " + arguments);
            return;
        }

        //Check arguments count
        short expectedArgsCount = optionalCommandSpec.get().getCommand().getClass().getAnnotation(BotCommand.class).argsCount();
        if (expectedArgsCount != 0 && expectedArgsCount != argsList.size())
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField(":warning: Poprawne użycie komendy: ", optionalCommandSpec.get().getCommand().getUsage(),false);
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
            return;
        }

        //Log
        logCommandUsage(member, channel, message);

        //Execute
        executeCommand(commandAlias, member, channel, argsList);
    }

    private void logCommandUsage(Member member, TextChannel channel, Message message)
    {
        LOGGER.info("Guild: {}, Channel: {}, Member: {}, Message: {}", channel.getGuild().getName(),
                channel.getName(), member.getEffectiveName(),
                message.getContentDisplay());
    }

    private Optional<CommandSpec> getCommandSpec(String commandAlias)
    {
        for (List<String> commandAliases : this.commands.keySet())
        {
            if (commandAliases.contains(commandAlias))
                return Optional.of(this.commands.get(commandAliases));
        }
        return Optional.empty();
    }
}
