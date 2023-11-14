package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
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

    /**
     * A method responsible for executing command logic.
     *
     * @param event the event to handle
     * @throws CommandException the exception
     */
    void onSlashCommand(SlashCommandInteractionEvent event) throws CommandException;

    /**
     * Interface implementations can implement this method to react to slash command button clicks.
     *
     * @param event the event to handle
     * @throws CommandException the exception
     */
    default void onButtonClick(ButtonInteractionEvent event) throws CommandException {}

    /**
     * Determines if implementation of this interface supports the given event.
     *
     * By default, it is determined by checking the slash command alias with {@link SlashCommand#getAliases()}
     *
     * @param event the event to handle
     * @return true if supports, false if not
     */
    default boolean supports(SlashCommandInteractionEvent event)
    {
        return event.getName().equals(getAliases().get(0));
    }

    /**
     * Determines if implementation of this interface supports the given button event.
     *
     * By default, returns false, as not every slash command uses buttons.
     *
     * @param event the event to handle
     * @return true if supports, false if not
     */
    default boolean supports(ButtonInteractionEvent event)
    {
        return false;
    }
}
