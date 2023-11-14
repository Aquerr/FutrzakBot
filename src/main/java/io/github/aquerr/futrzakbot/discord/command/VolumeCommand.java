package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.parameters.IntegerParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;

public class VolumeCommand implements Command, SlashCommand
{
    private static final String VOLUME_PARAM_KEY = "volume";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public VolumeCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        GuildMessageChannel channel = context.getGuildMessageChannel();
        final long guildId = channel.getGuild().getIdLong();
        setVolume(guildId, channel, context.require(VOLUME_PARAM_KEY));
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("volume");
    }

    @Override
    public String getName()
    {
        return this.messageSource.getMessage("command.volume.name");
    }

    @Override
    public String getDescription()
    {
        return this.messageSource.getMessage("command.volume.description");
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.INTEGER, VOLUME_PARAM_KEY, this.messageSource.getMessage("command.volume.slash.param.volume.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        setVolume(event.getGuild().getIdLong(), event.getChannel().asGuildMessageChannel(), event.getOption(VOLUME_PARAM_KEY).getAsInt());
        event.reply(this.messageSource.getMessage("command.volume.change")).complete();

    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(IntegerParameter.builder().key(VOLUME_PARAM_KEY).build());
    }

    private void setVolume(Long guildId, GuildMessageChannel channel, int volume)
    {
        this.futrzakAudioPlayerManager.setVolume(guildId, channel, volume);
    }

}
