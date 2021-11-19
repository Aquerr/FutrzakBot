package io.github.aquerr.futrzakbot.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand
public class InfoCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public InfoCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(textChannel.getGuild().getIdLong()).getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createNowPlayingMessage(audioTrack.getInfo().author, audioTrack.getInfo().title)).queue();
        }
        else
        {
            textChannel.sendMessage("Obecnie nie gra żaden utwór!").queue();
        }
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f info";
    }

    @Override
    public String getHelpName()
    {
        return ":info: Sprawdź obecnie grający utwór";
    }
}
