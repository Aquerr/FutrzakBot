package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.MemberParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.games.LoveMeter;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class LoveCommand implements Command, SlashCommand
{
    private static final String TARGET_PARAM_KEY = "target";

    private final MessageSource messageSource;

    @Override
    public boolean execute(CommandContext context)
    {
        Member member = context.getMember();
        TextChannel channel = context.getTextChannel();
        Member selectedMember = context.require(TARGET_PARAM_KEY);

        Message loveMessage = LoveMeter.checkLove(member, selectedMember);
        channel.sendMessage(loveMessage).queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("love");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.love.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.love.description");
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.USER, TARGET_PARAM_KEY, messageSource.getMessage("command.love.slash.param.target.desc"), true);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        event.reply(LoveMeter.checkLove(event.getMember(), event.getOption(TARGET_PARAM_KEY).getAsMember())).queue();
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(MemberParameter.builder().key(TARGET_PARAM_KEY).build());
    }
}
