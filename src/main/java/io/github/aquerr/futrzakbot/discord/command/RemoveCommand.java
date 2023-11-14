package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.IntegerParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;

public class RemoveCommand implements Command, SlashCommand
{
    private static final String TRACK_POSITION_PARAM_KEY = "track_position";
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public RemoveCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager,
                         MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        GuildMessageChannel channel = context.getGuildMessageChannel();
        futrzakAudioPlayerManager.removeElement(context.require(TRACK_POSITION_PARAM_KEY), context.getGuildMessageChannel().getGuild().getIdLong(), channel);
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("remove");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.remove.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.remove.description");
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.INTEGER, TRACK_POSITION_PARAM_KEY, this.messageSource.getMessage("command.remove.slash.param.track_position.desc"), true);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        final int trackPosition = event.getOption(TRACK_POSITION_PARAM_KEY).getAsInt();

        this.futrzakAudioPlayerManager.removeElement(trackPosition, event.getGuild().getIdLong(), event.getChannel().asGuildMessageChannel());
        event.reply(messageSource.getMessage("command.remove.removed-track", trackPosition)).queue();
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(IntegerParameter.builder().key(TRACK_POSITION_PARAM_KEY).build());
    }
}
