package io.github.aquerr.futrzakbot.events;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.command.CommandManager;
import io.github.aquerr.futrzakbot.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MessageListener extends ListenerAdapter
{
    private final FutrzakBot futrzakBot;

    public MessageListener(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        TextChannel textChannel = event.getTextChannel();
        Member member = event.getMember();

        // Bot cannot be used from webhooks and private channels
        if (member == null)
            return;

        if (isMessageWithFutrzakPrefix(event.getMessage().getContentDisplay()))
        {
            this.futrzakBot.getCommandManager().processCommand(member, textChannel, event.getMessage());
        }

        if(event.getMessage().getContentDisplay().contains("Kocham") || event.getMessage().getContentDisplay().contains("kocham") || event.getMessage().getContentDisplay().contains("lofki")
                || event.getMessage().getContentDisplay().contains("loffciam") || event.getMessage().getContentDisplay().contains("stellar"))
        {
            event.getMessage().addReaction("❤").queue();
        }
    }


    /**
     * Used for pagination in help command
     */
    @Override
    public void onGenericGuildMessageReaction(@NotNull GenericGuildMessageReactionEvent event)
    {
        // If it is Futrzak who added reaction... don't process further.
        if (this.futrzakBot.getJda().getSelfUser().getIdLong() == event.getUserIdLong())
            return;

        Message message = event.retrieveMessage().complete();
        if (message.getEmbeds().isEmpty())
            return;

        MessageEmbed messageEmbed = message.getEmbeds().get(0);

        if (isFutrzakHelpMessageReaction(messageEmbed))
        {
            String emoji = event.getReactionEmote().getAsCodepoints();
            if (emoji.equals(EmojiUnicodes.ARROW_LEFT))
            {
                // Get current page and move back
                int page = getCurrentHelpPage(messageEmbed);

                MessageEmbed newMessage = FutrzakMessageEmbedFactory.createHelpMessage(this.futrzakBot.getCommandManager().getCommands().values(), page - 1);
                message.editMessageEmbeds(newMessage).queue();
            }
            else if (emoji.equals(EmojiUnicodes.ARROW_RIGHT))
            {
                // Get current page and move forward
                int page = getCurrentHelpPage(messageEmbed);

                MessageEmbed newMessage = FutrzakMessageEmbedFactory.createHelpMessage(this.futrzakBot.getCommandManager().getCommands().values(), page + 1);
                message.editMessageEmbeds(newMessage).queue();
            }
        }
    }

    private boolean isMessageWithFutrzakPrefix(String message)
    {
        String[] words = message.split(" ");
        return words.length > 1 && words[0].equals(CommandManager.COMMAND_PREFIX);
    }

    private boolean isFutrzakHelpMessageReaction(MessageEmbed messageEmbed)
    {
        return Optional.ofNullable(messageEmbed.getTitle()).orElse("")
                .equals(FutrzakMessageEmbedFactory.HELP_MESSAGE_TITLE);
    }

    private int getCurrentHelpPage(MessageEmbed messageEmbed)
    {
        String footer = Optional.ofNullable(messageEmbed.getFooter())
                .map(MessageEmbed.Footer::getText)
                .orElse("");

        return Integer.parseInt(footer.split("\\/")[0]);
    }
}
