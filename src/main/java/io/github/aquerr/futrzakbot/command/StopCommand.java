package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class StopCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public StopCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        this.futrzakAudioPlayerManager.stop(textChannel.getGuild().getIdLong(), textChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("stop");
    }

    @Override
    public String getName()
    {
        return ":octagonal_sign: Zatrzymaj odtwarzacz muzyki: ";
    }

    @Override
    public String getDescription()
    {
        return "Zatrzymaj odtwarzacz muzyki";
    }
}
