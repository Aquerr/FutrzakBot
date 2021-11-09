package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand(argsCount = 1)
public class VolumeCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public VolumeCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        final long guildId = textChannel.getGuild().getIdLong();
        this.futrzakAudioPlayerManager.setVolume(guildId, textChannel, Integer.parseInt(args.get(0)));
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f volume <level>";
    }

    @Override
    public String getHelpName()
    {
        return ":loud_sound: Zmień głośność odtwarzacza: ";
    }
}
