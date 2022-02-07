package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand
public class ResumeCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public ResumeCommand(final FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        this.futrzakAudioPlayerManager.resume(textChannel.getGuild().getIdLong(), textChannel);
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f resume";
    }

    @Override
    public String getHelpName()
    {
        return ":play_pause: Wznów odtwarzacz";
    }
}
