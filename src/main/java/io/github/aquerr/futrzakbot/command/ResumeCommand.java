package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class ResumeCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public ResumeCommand(final FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.resume(textChannel.getGuild().getIdLong(), textChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("resume");
    }

    @Override
    public String getName()
    {
        return ":play_pause: Wznów odtwarzacz";
    }

    @Override
    public String getDescription()
    {
        return "Wznów odtwarzacz";
    }
}
