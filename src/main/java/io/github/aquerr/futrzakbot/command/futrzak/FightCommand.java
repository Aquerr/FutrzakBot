package io.github.aquerr.futrzakbot.command.futrzak;

import io.github.aquerr.futrzakbot.command.Command;
import io.github.aquerr.futrzakbot.command.CommandManager;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.command.context.CommandContext;

import java.util.Collections;
import java.util.List;

public class FightCommand implements Command
{
    private static final String ENEMY_PARAM_KEY = "enemy";

    @Override
    public boolean execute(CommandContext context)
    {
        context.getTextChannel().sendMessage("Ta funkcja nie została jeszcze w pełni dodana :/").complete();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("fight");
    }

    @Override
    public String getUsage()
    {
        return CommandManager.COMMAND_PREFIX + " fight <użytkownik>";
    }

    @Override
    public String getName()
    {
        return ":crossed_swords: Walcz z futrzakiem innej osoby: ";
    }

    @Override
    public String getDescription()
    {
        return "Walcz z innym futrzakiem";
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(ENEMY_PARAM_KEY).build());
    }
}
