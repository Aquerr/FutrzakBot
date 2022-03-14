package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class ClearCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public ClearCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.clearQueue(textChannel.getGuild().getIdLong(),textChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("clear");
    }

    @Override
    public String getName()
    {
        return ":octagonal_sign: Wyczyść kolejkę ";
    }

    @Override
    public String getDescription()
    {
        return "Wyczyćś kolejkę!";
    }
}
