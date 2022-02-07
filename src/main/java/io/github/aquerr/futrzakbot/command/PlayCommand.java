package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

@BotCommand(argsCount = 1)
public class PlayCommand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public PlayCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(Member member, TextChannel textChannel, List<String> args)
    {
        Guild guild = textChannel.getGuild();
        GuildVoiceState guildVoiceState = member.getVoiceState();
        VoiceChannel voiceChannel = guildVoiceState.getChannel();
        if (voiceChannel == null)
        {
            textChannel.sendMessage("Aby użyć tej komendy musisz być na kanale głosowym!").complete();
        }
        else
        {
            AudioManager audioManager = guild.getAudioManager();
            if (audioManager.getSendingHandler() == null)
            {
                audioManager.setSendingHandler(new AudioPlayerSendHandler(this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(guild.getIdLong()).getInternalAudioPlayer()));
            }
            audioManager.openAudioConnection(voiceChannel);
            String songName = args.get(0);
            this.futrzakAudioPlayerManager.queue(guild.getIdLong(), textChannel, songName);
        }
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f play <utwór>";
    }

    @Override
    public String getHelpName()
    {
        return ":microphone2: Dodaj utwór do kolejki odtwarzacza: ";
    }
}
