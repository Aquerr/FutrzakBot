package io.github.aquerr.futrzakbot.command;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface SlashCommand
{
    CommandData getSlashCommandData();

    boolean onSlashCommand(SlashCommandEvent event);

    boolean onButtonClick(ButtonClickEvent event);

    boolean supports(SlashCommandEvent event);

    boolean supports(ButtonClickEvent event);
}
