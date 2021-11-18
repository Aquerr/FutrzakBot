package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

@BotCommand
public class QueueCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public QueueCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        textChannel.sendMessageEmbeds(FutrzakMessageEmbedFactory.createQueueMessage(this.futrzakAudioPlayerManager.getQueue(textChannel.getGuild().getIdLong()))).queue();
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f queue";
    }

    @Override
    public String getHelpName()
    {
        return ":notes: Sprawdź kolejkę utworów w odtwarzaczu";
    }
}
