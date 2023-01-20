package io.github.aquerr.futrzakbot.discord.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.CommandManager;
import io.github.aquerr.futrzakbot.discord.command.SlashCommand;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class SlashCommandListener extends ListenerAdapter
{
    private final CommandManager commandManager;
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    public SlashCommandListener(final CommandManager commandManager,
                                final FutrzakAudioPlayerManager futrzakAudioPlayerManager,
                                final FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.commandManager = commandManager;
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageEmbedFactory = messageEmbedFactory;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        for (final SlashCommand slashCommand : this.commandManager.getSlashCommands())
        {
            if(slashCommand.supports(event))
            {
                slashCommand.onSlashCommand(event);
                break;
            }
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        for (final SlashCommand slashCommand : this.commandManager.getSlashCommands())
        {
            if(slashCommand.supports(event))
            {
                slashCommand.onButtonClick(event);
                break;
            }
        }

        if (event.getComponentId().equals("queue")) {
            List<AudioTrack> queue = this.futrzakAudioPlayerManager.getQueue(event.getGuild().getIdLong());
            event.replyEmbeds(messageEmbedFactory.createQueueMessage(queue)).queue();
        } else if (event.getComponentId().equals("next")) {
            this.futrzakAudioPlayerManager.skipAndPlayNextTrack(event.getGuild().getIdLong(), event.getTextChannel());
            event.reply("next track").queue();
        }
    }
}
