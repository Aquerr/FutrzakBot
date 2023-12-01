package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.StringParameter;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;

public class JumpToCommand implements Command, SlashCommand
{

    private static final String JUMPTO_PARAM_KEY = "jumpto";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final MessageSource messageSource;

    public JumpToCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager, MessageSource messageSource) {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext commandContext) throws CommandException {
        return false;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("jumpto");
    }

    @Override
    public String getName() {
        return this.messageSource.getMessage("command.jumpto.name");
    }

    @Override
    public String getDescription() {
        return this.messageSource.getMessage("command.jumpto.description");
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.STRING, JUMPTO_PARAM_KEY, this.messageSource.getMessage("command.jumpto.slash.param.jumpto.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        int time;
        if (event.getOption(JUMPTO_PARAM_KEY).getAsString().contains(":")) {
            String[] times = event.getOption(JUMPTO_PARAM_KEY).getAsString().split(":", 2);
            if (times[1].length() > 2) {
                event.reply(this.messageSource.getMessage("error.command.jumpto.wrong-time-format")).queue();
                return;
            }
            time = Integer.parseInt(times[0])*60 + Integer.parseInt(times[1]);
        }
        else {
            time = event.getOption(JUMPTO_PARAM_KEY).getAsInt();
        }
        jumpTo(event.getGuild().getIdLong(), event.getChannel().asTextChannel(), time);
        event.reply(this.messageSource.getMessage("command.jumpto.change")).complete();

    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(StringParameter.builder().key(JUMPTO_PARAM_KEY).build());
    }

    private void jumpTo(Long guildId, GuildMessageChannel channel, int time)
    {
        this.futrzakAudioPlayerManager.jumpTo(guildId, channel, time);
    }
}

