package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.games.WebGame;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class GameCommand implements Command, SlashCommand
{
    private final MessageSource messageSource;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    private final WebGame webGame;

    @Override
    public boolean execute(CommandContext commandContext) throws CommandException
    {
        TextChannel textChannel = commandContext.getTextChannel();
        textChannel.sendMessage("Losuję grę...").queue();
        textChannel.sendMessage(webGame.requestWebGameForUser(commandContext.getMember().getUser())).queue();
        return false;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("game");
    }

    @Override
    public String getName()
    {
        return ":video_game: Wylosowanie gry przez futrzaka";
    }

    @Override
    public String getDescription()
    {
        return "Wylosuj grę";
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        event.reply("Link do Twojej wylosowanej gry: " + webGame.requestWebGameForUser(event.getUser())).queue();
    }
}
