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
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    public InfoCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageEmbedFactory = messageEmbedFactory;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(textChannel.getGuild().getIdLong()).getPlayingTrack();
        if (audioTrack != null)
        {
            textChannel.sendMessageEmbeds(messageEmbedFactory.createNowPlayingMessage(audioTrack)).complete();
        }
        else
        {
            textChannel.sendMessage("Obecnie nie gra żaden utwór!").complete();
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
