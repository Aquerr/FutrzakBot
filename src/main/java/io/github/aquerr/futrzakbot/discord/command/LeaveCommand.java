package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class LeaveCommand implements Command, SlashCommand
{
    private final MessageSource messageSource;
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    @Override
    public boolean execute(CommandContext commandContext) throws CommandException
    {
        this.futrzakAudioPlayerManager.disconnect(commandContext.getTextChannel().getGuild().getIdLong());
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("leave");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.leave.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.leave.description");
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        this.futrzakAudioPlayerManager.disconnect(event.getGuild().getIdLong());
        event.reply(messageSource.getMessage("command.leave.leaving")).complete().deleteOriginal().queue();
    }
}
