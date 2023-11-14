package io.github.aquerr.futrzakbot.discord.command.listener;

import io.github.aquerr.futrzakbot.discord.command.CommandManager;
import io.github.aquerr.futrzakbot.discord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter
{
    private final CommandManager commandManager;

    public SlashCommandListener(final CommandManager commandManager)
    {
        this.commandManager = commandManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        for (final SlashCommand slashCommand : this.commandManager.getSlashCommands())
        {
            if(slashCommand.supports(event))
            {
                this.commandManager.processSlashCommand(slashCommand, event);
                break;
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        for (final SlashCommand slashCommand : this.commandManager.getSlashCommands())
        {
            if(slashCommand.supports(event))
            {
                this.commandManager.processSlashButtons(slashCommand, event);
                break;
            }
        }
    }
}
