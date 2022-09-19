package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Collections;
import java.util.List;

public class PlayCommand implements Command, SlashCommand
{
    private static final String SONG_PARAM_KEY = "song";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public PlayCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        Member member = context.getMember();


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
            String songName = context.require(SONG_PARAM_KEY);
            this.futrzakAudioPlayerManager.queue(guild.getIdLong(), textChannel, songName);
        }
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("play");
    }

    @Override
    public String getUsage()
    {
        return CommandManager.COMMAND_PREFIX + " play <utwór>";
    }

    @Override
    public String getName()
    {
        return ":microphone2: Dodaj utwór do kolejki odtwarzacza: ";
    }

    @Override
    public String getDescription()
    {
        return "Dodaj podany utwór do kolejki odtwarzacza";
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(SONG_PARAM_KEY).build());
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return new CommandData("player", "Open player menu")
                            .addOption(OptionType.STRING, "song", "Enter song name to play", false)
                            .setDefaultEnabled(true);
    }

    @Override
    public boolean onSlashCommand(SlashCommandEvent event)
    {
        return false;
    }

    @Override
    public boolean onButtonClick(ButtonClickEvent event)
    {
        return false;
    }

    @Override
    public boolean supports(SlashCommandEvent event)
    {
        return false;
    }

    @Override
    public boolean supports(ButtonClickEvent event)
    {
        return false;
    }
}
