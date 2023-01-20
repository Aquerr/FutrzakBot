package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class RemoveComand implements Command
{
    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private static final String ELEMENT_PARAM_KEY = "utwór";

    public RemoveComand(FutrzakAudioPlayerManager futrzakAudioPlayerManager)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel textChannel = context.getTextChannel();
        futrzakAudioPlayerManager.removeElement(context.require(ELEMENT_PARAM_KEY), context.getTextChannel().getGuild().getIdLong(), textChannel);
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
        return ":wastebasket: Wyrzuć utwór";
    }

    @Override
    public String getDescription()
    {
        return "Usuń utwór z kolejki.";
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(Parameter.builder().key(ELEMENT_PARAM_KEY).type(Integer.class).build());
    }
}
