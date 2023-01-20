package io.github.aquerr.futrzakbot.discord.command;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface SlashCommand
{
    /**
     * Aliases of the command.
     *
     * Implementations should provide aliases which can be used to execute command.
     * @return the list of command aliases
     */
    List<String> getAliases();

    /**
     * The description of the command. Mostly used in HelpCommand.
     *
     * @return the description of the command.
     */
    String getDescription();

    default CommandData getSlashCommandData()
    {
        return new CommandData(getAliases().get(0), getDescription())
                .setDefaultEnabled(true);
    }

    void onSlashCommand(SlashCommandEvent event);

    default void onButtonClick(ButtonClickEvent event) {};

    default boolean supports(SlashCommandEvent event)
    {
        return event.getName().equals(getAliases().get(0));
    }

    default boolean supports(ButtonClickEvent event)
    {
        return false;
    }
}
