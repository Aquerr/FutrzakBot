package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QueueCommand implements Command, SlashCommand
{
    private static final String SONG_PARAM_KEY = "song";
    private static final String MUST_BE_ON_VOICE_CHANNEL = "error.command.must-be-on-voice-channel";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public QueueCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        String songName = context.<String>get(SONG_PARAM_KEY).orElse(null);
        TextChannel textChannel = context.getTextChannel();

        if (songName != null)
        {
            Member member = context.getMember();
            Guild guild = textChannel.getGuild();
            GuildVoiceState guildVoiceState = member.getVoiceState();
            VoiceChannel voiceChannel = guildVoiceState.getChannel();
            if (voiceChannel == null)
            {
                textChannel.sendMessage(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).complete();
                return false;
            }

            queueTrack(guild, textChannel, voiceChannel, songName);
            return true;
        }

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
        return this.messageSource.getMessage("command.queue.name");
    }

    @Override
    public String getDescription()
    {
        return this.messageSource.getMessage("command.queue.description");
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.STRING, SONG_PARAM_KEY, this.messageSource.getMessage("command.queue.slash.param.song.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        String songName = Optional.ofNullable(event.getOption(SONG_PARAM_KEY))
                .map(OptionMapping::getAsString)
                .orElse(null);

        if (songName != null)
        {
            GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
            VoiceChannel voiceChannel = guildVoiceState.getChannel();
            if (voiceChannel == null)
            {
                event.reply(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).queue();
                return;
            }
            event.reply(this.messageSource.getMessage("command.play.adding")).complete();
            queueTrack(event.getGuild(), event.getTextChannel(), voiceChannel, songName);
        }

        event.deferReply().addEmbeds(FutrzakMessageEmbedFactory.createQueueMessage(this.futrzakAudioPlayerManager.getQueue(event.getTextChannel().getGuild().getIdLong()))).queue();
    }

    private void queueTrack(Guild guild, TextChannel textChannel, VoiceChannel voiceChannel, String songName)
    {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.getSendingHandler() == null)
        {
            audioManager.setSendingHandler(new AudioPlayerSendHandler(this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(guild.getIdLong()).getInternalAudioPlayer()));
        }
        audioManager.openAudioConnection(voiceChannel);
        this.futrzakAudioPlayerManager.queue(guild.getIdLong(), textChannel, songName, false);
    }
}
