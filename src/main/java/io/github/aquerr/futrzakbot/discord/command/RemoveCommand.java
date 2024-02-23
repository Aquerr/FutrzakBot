package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.IntegerParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RemoveCommand implements Command, SlashCommand
{
    private static final String TRACK_POSITION_PARAM_KEY = "track_position";
    private static final String NAME_PARAM_KEY = "name";
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
                .addOption(OptionType.INTEGER, TRACK_POSITION_PARAM_KEY, this.messageSource.getMessage("command.remove.slash.param.track_position.desc"), false)
                .addOption(OptionType.STRING, NAME_PARAM_KEY, this.messageSource.getMessage("command.remove.slash.param.name.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        Optional<OptionMapping> trackPosition = Optional.ofNullable(event.getOption(TRACK_POSITION_PARAM_KEY));
        Optional<OptionMapping> trackName = Optional.ofNullable(event.getOption(NAME_PARAM_KEY));
        if (trackPosition.isPresent())
        {
            int position = trackPosition.get().getAsInt();
            this.futrzakAudioPlayerManager.removeElement(position, event.getGuild().getIdLong(), event.getChannel().asGuildMessageChannel());
            event.reply(messageSource.getMessage("command.remove.removed-track-position", position)).queue();
        }
        else if (trackName.isPresent())
        {
            String name = trackName.get().getAsString();
            this.futrzakAudioPlayerManager.removeElement(name, event.getGuild().getIdLong(), event.getChannel().asGuildMessageChannel());
            event.reply(messageSource.getMessage("command.remove.removed-track-name", name)).queue();
        }

    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(IntegerParameter.builder().key(TRACK_POSITION_PARAM_KEY).build());
    }
}
