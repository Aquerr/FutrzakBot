package io.github.aquerr.futrzakbot.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class InfoCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public InfoCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(textChannel.getGuild().getIdLong()).getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createNowPlayingMessage(audioTrack)).queue();
        }
        else
        {
            textChannel.sendMessage("Obecnie nie gra żaden utwór!").queue();
        }
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("info");
    }

    @Override
    public String getName()
    {
        return ":info: Sprawdź obecnie grający utwór";
    }

    @Override
    public String getDescription()
    {
        return "Sprawdź obecnie grający utwór";
    }
}
