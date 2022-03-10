package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.games.LoveMeter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class LoveCommand implements Command
{
    private static final String PARAM_KEY = "message";

    @Override
    public boolean execute(CommandContext context)
    {
        Member member = context.getMember();
        TextChannel channel = context.getTextChannel();
        String message = context.require(PARAM_KEY);

        Message loveMessage = LoveMeter.checkLove(member, message);
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
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(PARAM_KEY).build());
    }
}
