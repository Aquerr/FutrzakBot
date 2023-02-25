package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

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
        TextChannel textChannel = context.getTextChannel();
        Member member = context.getMember();

        GuildVoiceState guildVoiceState = member.getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel();
        if (voiceChannel == null)
        {
            textChannel.sendMessage(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).complete();
            return false;
        }
        resumePlayer(context.getTextChannel(), voiceChannel);
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
    public void onSlashCommand(SlashCommandEvent event)
    {
        GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel();
        if (voiceChannel == null)
        {
            event.reply(this.messageSource.getMessage(MUST_BE_ON_VOICE_CHANNEL)).queue();
            return;
        }

        event.reply(messageSource.getMessage("command.resume.resuming")).complete();
        resumePlayer(event.getTextChannel(), voiceChannel);
    }

    private void resumePlayer(TextChannel textChannel, VoiceChannel voiceChannel)
    {
        this.futrzakAudioPlayerManager.resume(textChannel.getGuild().getIdLong(), textChannel, voiceChannel);
    }
}
