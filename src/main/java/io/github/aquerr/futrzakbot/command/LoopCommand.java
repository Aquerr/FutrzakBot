package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class LoopCommand implements Command
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
        futrzakAudioPlayerManager.toggleLoop(textChannel.getGuild().getIdLong(),textChannel);
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
}
