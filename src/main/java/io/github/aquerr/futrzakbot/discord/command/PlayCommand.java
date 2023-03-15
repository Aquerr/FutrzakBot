package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.AudioSource;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collections;
import java.util.List;

public class PlayCommand implements Command, SlashCommand
{
    private static final String SONG_PARAM_KEY = "song";
    private static final String SOUNDCLOUD_SONG_PARAM_KEY = "soundcloud";
    private static final String YOUTUBE_SONG_PARAM_KEY = "youtube";
    private static final String MUST_BE_ON_VOICE_CHANNEL = "error.command.must-be-on-voice-channel";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    private final MessageSource messageSource;

    public PlayCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        String songName = context.require(SONG_PARAM_KEY);
        TextChannel textChannel = context.getTextChannel();
        Member member = context.getMember();

        Guild guild = textChannel.getGuild();
        GuildVoiceState guildVoiceState = member.getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel();
        if (voiceChannel == null)
        {
            textChannel.sendMessage(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).complete();
            return false;
        }

        songName = SongParamHelper.getIdentifierForTrack(songName, AudioSource.UNKNOWN);
        queueTrack(guild, textChannel, voiceChannel, member, songName);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("play");
    }

    @Override
    public String getName()
    {
        return this.messageSource.getMessage("command.play.name");
    }

    @Override
    public String getDescription()
    {
        return this.messageSource.getMessage("command.play.description");
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(SONG_PARAM_KEY).build());
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.STRING, SONG_PARAM_KEY, this.messageSource.getMessage("command.play.slash.param.song.desc"), false)
                .addOption(OptionType.STRING, SOUNDCLOUD_SONG_PARAM_KEY, this.messageSource.getMessage("command.play.slash.param.soundcloud.desc"), false)
                .addOption(OptionType.STRING, YOUTUBE_SONG_PARAM_KEY, this.messageSource.getMessage("command.play.slash.param.youtube.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        String songName = SongParamHelper.getSongNameFromSlashEvent(event);
        GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel();
        if (voiceChannel == null)
        {
            event.reply(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).queue();
            return;
        }

        event.reply(this.messageSource.getMessage("command.play.adding")).complete();
        queueTrack(event.getGuild(), event.getTextChannel(), voiceChannel, event.getMember(), songName);
    }

    private void queueTrack(Guild guild, TextChannel textChannel, VoiceChannel voiceChannel, Member member, String songName)
    {
        this.futrzakAudioPlayerManager.queue(guild, textChannel, voiceChannel, member, songName, true);
    }
}
