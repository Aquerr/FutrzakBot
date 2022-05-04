package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.exception.CommandException;
import io.github.aquerr.futrzakbot.games.ValheimGame;
import io.github.aquerr.futrzakbot.message.MessageSource;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ValheimCommand implements Command
{
    private final MessageSource messageSource;
    private final ValheimGame valheimGame;

    @Override
    public boolean execute(CommandContext commandContext) throws CommandException
    {
        valheimGame.checkValheimServerStatus();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("valheim");
    }

    @Override
    public String getName()
    {
        return "Valheim";
    }

    @Override
    public String getDescription()
    {
        return "Check Valheim server status";
    }
}
