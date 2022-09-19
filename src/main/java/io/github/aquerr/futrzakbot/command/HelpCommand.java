package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HelpCommand implements Command, SlashCommand
{
    private static final String BUTTON_HELP_LEFT_ID = "help_left";
    private static final String BUTTON_HELP_RIGHT_ID = "help_rigth";

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

    @Override
    public CommandData getSlashCommandData()
    {
        return new CommandData(getAliases().get(0), getDescription())
                .setDefaultEnabled(true);
    }

    @Override
    public boolean onSlashCommand(SlashCommandEvent event)
    {
        if (event.getName().equals(getAliases().get(0)))
        {
            event.deferReply().addEmbeds(buildHelpMessage())
                    .addActionRow(Button.primary(BUTTON_HELP_LEFT_ID, Emoji.fromUnicode(EmojiUnicodes.ARROW_LEFT)),
                                    Button.primary(BUTTON_HELP_RIGHT_ID, Emoji.fromUnicode(EmojiUnicodes.ARROW_RIGHT)))
                    .queue();
            return true;
        }
        return false;
    }

    @Override
    public boolean onButtonClick(ButtonClickEvent event)
    {
        if (event.getComponentId().equals(BUTTON_HELP_LEFT_ID))
        {
            Message message = event.getMessage();
            if (message.getEmbeds().isEmpty())
                return true;

            MessageEmbed messageEmbed = message.getEmbeds().get(0);

            // Get current page and move back
            int page = getCurrentHelpPage(messageEmbed);

            MessageEmbed newMessage = FutrzakMessageEmbedFactory.createHelpMessage(this.commandManager.getCommands().values(), page - 1);
            event.editMessageEmbeds(newMessage).queue();
            return true;
        }
        else if (event.getComponentId().equals(BUTTON_HELP_RIGHT_ID))
        {
            Message message = event.getMessage();
            if (message.getEmbeds().isEmpty())
                return true;

            MessageEmbed messageEmbed = message.getEmbeds().get(0);

            // Get current page and move forward
            int page = getCurrentHelpPage(messageEmbed);

            MessageEmbed newMessage = FutrzakMessageEmbedFactory.createHelpMessage(this.commandManager.getCommands().values(), page + 1);
            event.editMessageEmbeds(newMessage).queue();
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(SlashCommandEvent event)
    {
        return event.getName().equals(getAliases().get(0));
    }

    @Override
    public boolean supports(ButtonClickEvent event)
    {
        return event.getComponentId().equals(BUTTON_HELP_LEFT_ID) || event.getComponentId().equals(BUTTON_HELP_RIGHT_ID);
    }

    private int getCurrentHelpPage(MessageEmbed messageEmbed)
    {
        String footer = Optional.ofNullable(messageEmbed.getFooter())
                .map(MessageEmbed.Footer::getText)
                .orElse("");

        return Integer.parseInt(footer.split("\\/")[0]);
    }
}
