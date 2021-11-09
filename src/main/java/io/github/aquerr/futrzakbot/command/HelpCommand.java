package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;
import java.util.Map;

@BotCommand
public class HelpCommand implements Command
{
    private final CommandManager commandManager;

    HelpCommand(CommandManager commandManager)
    {
        this.commandManager = commandManager;
    }

    @Override
    public boolean execute(Member member, TextChannel channel, List<String> args)
    {
        Map<List<String>, CommandSpec> commands = this.commandManager.getCommands();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Futrzak - Zabawny bot", "https://github.com/Aquerr/FutrzakBot");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Oto spis komend, dostępnych u futrzaka: ");

        for (final CommandSpec commandSpec : commands.values())
        {
            embedBuilder.addField(commandSpec.getCommand().getHelpName(), commandSpec.getCommand().getUsage(), false);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();

        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f help";
    }

    @Override
    public String getHelpName()
    {
        return "Komendy";
    }
}
