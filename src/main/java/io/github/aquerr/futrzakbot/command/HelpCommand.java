package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command
{
    private final MessageSource messageSource;
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager, MessageSource messageSource)
    {
        this.commandManager = commandManager;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext commandContext)
    {
        Message message = commandContext.getTextChannel().sendMessageEmbeds(buildHelpMessage()).complete();
        message.addReaction(EmojiUnicodes.ARROW_LEFT).complete();
        message.addReaction(EmojiUnicodes.ARROW_RIGHT).complete();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("help");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.help.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.help.description");
    }

    private MessageEmbed buildHelpMessage()
    {
        Map<List<String>, Command> commands = this.commandManager.getCommands();
        return FutrzakMessageEmbedFactory.createHelpMessage(commands.values(), 1);
    }
}
