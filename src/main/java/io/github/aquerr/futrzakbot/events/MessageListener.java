package io.github.aquerr.futrzakbot.events;

import io.github.aquerr.futrzakbot.FutrzakBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
    private static final String FUTRZAK_BOT_COMMAND_PREFIX = "!f";

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

    private boolean isMessageWithFutrzakPrefix(String message)
    {
        String[] words = message.split(" ");
        return words.length > 1 && words[0].equals(FUTRZAK_BOT_COMMAND_PREFIX);
    }
}
