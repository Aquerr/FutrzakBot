package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command
{
    private final CommandManager commandManager;

    HelpCommand(CommandManager commandManager)
    {
        this.commandManager = commandManager;
    }

    @Override
    public boolean execute(CommandContext commandContext)
    {
        Map<List<String>, Command> commands = this.commandManager.getCommands();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Futrzak - Zabawny bot", "https://github.com/Aquerr/FutrzakBot");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Oto spis komend, dostępnych u futrzaka: ");

        for (final Command command : commands.values())
        {
            embedBuilder.addField(command.getName(), command.getUsage(), false);
        }

        commandContext.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("help");
    }

    @Override
    public String getName()
    {
        return "Komendy";
    }

    @Override
    public String getDescription()
    {
        return "Pokazuje wszystkie dostępne komendy";
    }
}
