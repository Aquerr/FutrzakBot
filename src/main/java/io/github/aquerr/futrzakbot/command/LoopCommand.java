package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;

public class LoopCommand implements Command, SlashCommand
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public LoopCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        boolean newLoopState = futrzakAudioPlayerManager.toggleLoop(textChannel.getGuild().getIdLong(),textChannel);
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createLoopMessage(newLoopState)).queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("loop");
    }

    @Override
    public String getName()
    {
        return ":infinity: Zapętl Kolejke";
    }

    @Override
    public String getDescription()
    {
        return "Zapętlanie kolejki";
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        boolean newLoopState = futrzakAudioPlayerManager.toggleLoop(event.getGuild().getIdLong(), event.getTextChannel());
        event.replyEmbeds(FutrzakMessageEmbedFactory.createLoopMessage(newLoopState)).queue();
    }
}
