package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand
public class SkipCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public SkipCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        this.futrzakAudioPlayerManager.skipAndPlayNextTrack(textChannel.getGuild().getIdLong(), textChannel);
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f skip";
    }

    @Override
    public String getHelpName()
    {
        return ":track_next: Pomiń utwór: ";
    }
}
