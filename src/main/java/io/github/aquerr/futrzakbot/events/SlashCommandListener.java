package io.github.aquerr.futrzakbot.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.command.CommandManager;
import io.github.aquerr.futrzakbot.command.SlashCommand;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class SlashCommandListener extends ListenerAdapter
{
    private final CommandManager commandManager;
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public SlashCommandListener(final CommandManager commandManager,
                                final FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.commandManager = commandManager;
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        for (final SlashCommand slashCommand : this.commandManager.getSlashCommands())
        {
            if (slashCommand.onSlashCommand(event))
                break;
        }

        if (event.getName().equals("player")) {
            if (event.getOption("song") != null)
            {
                GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
                VoiceChannel voiceChannel = guildVoiceState.getChannel();
                if (voiceChannel == null)
                {
                    event.reply("Aby użyć tej komendy musisz być na kanale głosowym!").queue();
                }
                Guild guild = event.getGuild();
                AudioManager audioManager = guild.getAudioManager();
                audioManager.setSendingHandler(new AudioPlayerSendHandler(this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(guild.getIdLong()).getInternalAudioPlayer()));
                audioManager.openAudioConnection(voiceChannel);
                this.futrzakAudioPlayerManager.queue(event.getGuild().getIdLong(), event.getTextChannel(), event.getOption("song").getAsString());
            }

            event.reply("Futrzak Song Player")
                    .addActionRow(
                            Button.primary("queue", "View Song Queue"), // Button with only a label
                            Button.success("next", ":track_next:")) // Button with only an emoji
                    .queue();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        for (final SlashCommand slashCommand : this.commandManager.getSlashCommands())
        {
            if (slashCommand.onButtonClick(event))
                break;
        }

        if (event.getComponentId().equals("queue")) {
            List<AudioTrack> queue = this.futrzakAudioPlayerManager.getQueue(event.getGuild().getIdLong());
            event.replyEmbeds(FutrzakMessageEmbedFactory.createQueueMessage(queue)).queue();
        } else if (event.getComponentId().equals("next")) {
            this.futrzakAudioPlayerManager.skipAndPlayNextTrack(event.getGuild().getIdLong(), event.getTextChannel());
            event.reply("next track").queue();
        }
    }
}
