package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class QueueCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public QueueCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createQueueMessage(this.futrzakAudioPlayerManager.getQueue(textChannel.getGuild().getIdLong()))).queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("queue");
    }

    @Override
    public String getName()
    {
        return ":notes: Sprawdź kolejkę utworów w odtwarzaczu";
    }

    @Override
    public String getDescription()
    {
        return "Sprawdź kolejkę utworów w odtwarzaczu";
    }
}
