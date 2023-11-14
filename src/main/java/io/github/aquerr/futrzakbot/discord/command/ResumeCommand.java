package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

public class ResumeCommand implements Command, SlashCommand
{
    private static final String MUST_BE_ON_VOICE_CHANNEL = "error.command.must-be-on-voice-channel";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public ResumeCommand(final FutrzakAudioPlayerManager futrzakAudioPlayerManager, final MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        GuildMessageChannel channel = context.getGuildMessageChannel();
        Member member = context.getMember();

        GuildVoiceState guildVoiceState = member.getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        if (voiceChannel == null)
        {
            channel.sendMessage(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).complete();
            return false;
        }
        resumePlayer(context.getGuildMessageChannel(), voiceChannel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("resume");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.resume.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.resume.description");
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        if (voiceChannel == null)
        {
            event.reply(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).queue();
            return;
        }

        event.reply(messageSource.getMessage("command.resume.resuming")).complete();
        resumePlayer(event.getChannel().asGuildMessageChannel(), voiceChannel);
    }

    private void resumePlayer(GuildMessageChannel channel, VoiceChannel voiceChannel)
    {
        this.futrzakAudioPlayerManager.resume(channel.getGuild().getIdLong(), channel, voiceChannel);
    }
}
