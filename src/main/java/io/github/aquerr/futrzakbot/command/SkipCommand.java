package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class SkipCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public SkipCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.skipAndPlayNextTrack(textChannel.getGuild().getIdLong(), textChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("skip");
    }

    @Override
    public String getName()
    {
        return ":track_next: Pomiń utwór: ";
    }

    @Override
    public String getDescription()
    {
        return "Pomiń utwór";
    }
}
