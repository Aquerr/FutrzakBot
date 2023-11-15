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
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        GuildMessageChannel channel = context.getGuildMessageChannel();
        Member member = context.getMember();

        Guild guild = channel.getGuild();
        if (member.getVoiceState() == null || member.getVoiceState().getChannel() == null)
        {
            channel.sendMessage(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).complete();
            return false;
        }

        VoiceChannel voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
        songName = SongParamHelper.getIdentifierForTrack(songName, AudioSource.UNKNOWN);
        queueTrack(guild, channel, voiceChannel, member, songName);
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
    public SlashCommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.STRING, SONG_PARAM_KEY, this.messageSource.getMessage("command.play.slash.param.song.desc"), false)
                .addOption(OptionType.STRING, SOUNDCLOUD_SONG_PARAM_KEY, this.messageSource.getMessage("command.play.slash.param.soundcloud.desc"), false)
                .addOption(OptionType.STRING, YOUTUBE_SONG_PARAM_KEY, this.messageSource.getMessage("command.play.slash.param.youtube.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        String songName = SongParamHelper.getSongNameFromSlashEvent(event);
        GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
        VoiceChannel voiceChannel = Optional.ofNullable(guildVoiceState.getChannel())
                .map(AudioChannelUnion::asVoiceChannel)
                .orElse(null);

        if (voiceChannel == null)
        {
            event.reply(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).queue();
            return;
        }

        event.reply(this.messageSource.getMessage("command.play.adding")).complete();
        queueTrack(event.getGuild(), event.getChannel().asGuildMessageChannel(), voiceChannel, event.getMember(), songName);
    }

    private void queueTrack(Guild guild, GuildMessageChannel channel, VoiceChannel voiceChannel, Member member, String songName)
    {
        this.futrzakAudioPlayerManager.queue(guild, channel, voiceChannel, member, songName, true);
    }
}
