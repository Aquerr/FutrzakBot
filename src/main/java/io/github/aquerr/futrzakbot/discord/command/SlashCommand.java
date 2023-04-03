package io.github.aquerr.futrzakbot.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

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

    default SlashCommandData getSlashCommandData()
    {
        return Commands.slash(getAliases().get(0), getDescription())
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);
    }

    void onSlashCommand(SlashCommandInteractionEvent event);

    default void onButtonClick(ButtonInteractionEvent event) {};

    default boolean supports(SlashCommandInteractionEvent event)
    {
        return event.getName().equals(getAliases().get(0));
    }

    default boolean supports(ButtonInteractionEvent event)
    {
        return false;
    }
}
