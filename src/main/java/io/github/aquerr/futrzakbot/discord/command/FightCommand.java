package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

public class FightCommand implements Command, SlashCommand
{
    private static final String ENEMY_PARAM_KEY = "enemy";

    private final MessageSource messageSource;

    public FightCommand(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        context.getGuildMessageChannel().sendMessageEmbeds(
                FutrzakMessageEmbedFactory.getInstance().createFunctionNotImplementedMessage())
                .queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("fight");
    }

    @Override
    public String getName()
    {
        return this.messageSource.getMessage("command.fight.name");
    }

    @Override
    public String getDescription()
    {
        return this.messageSource.getMessage("command.fight.description");
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws CommandException
    {
        event.replyEmbeds(FutrzakMessageEmbedFactory.getInstance().createFunctionNotImplementedMessage()).queue();
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(ENEMY_PARAM_KEY).build());
    }
}
