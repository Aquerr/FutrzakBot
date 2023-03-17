package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.MemberParameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.games.LoveMeter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collections;
import java.util.List;

public class LoveCommand implements Command, SlashCommand
{
    private static final String PARAM_KEY = "użytkownik";

    @Override
    public boolean execute(CommandContext context)
    {
        Member member = context.getMember();
        TextChannel channel = context.getTextChannel();
        Member selectedMember = context.require(PARAM_KEY);

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
    public String getUsage()
    {
        return CommandManager.COMMAND_PREFIX + " love <użytkownik>";
    }

    @Override
    public String getName()
    {
        return ":heart: Licznik miłości: ";
    }

    @Override
    public String getDescription()
    {
        return "Licznik miłości";
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.USER, PARAM_KEY, "Cel", true);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        event.reply(LoveMeter.checkLove(event.getMember(), event.getOption(PARAM_KEY).getAsMember())).queue();
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(MemberParameter.builder().key(PARAM_KEY).build());
    }
}
