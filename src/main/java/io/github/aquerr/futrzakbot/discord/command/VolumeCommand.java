package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.parameters.IntegerParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Collections;
import java.util.List;

public class VolumeCommand implements Command
{
    private static final String VOLUME_PARAM_KEY = "volume";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;

    public VolumeCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        final long guildId = textChannel.getGuild().getIdLong();
        this.futrzakAudioPlayerManager.setVolume(guildId, textChannel, context.require(VOLUME_PARAM_KEY));
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("volume");
    }

    @Override
    public String getUsage()
    {
        return CommandManager.COMMAND_PREFIX + " volume <level>";
    }

    @Override
    public String getName()
    {
        return ":loud_sound: Zmień głośność odtwarzacza: ";
    }

    @Override
    public String getDescription()
    {
        return "Zmień głośność odtwarzacza";
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(IntegerParameter.builder().key(VOLUME_PARAM_KEY).build());
    }
}
