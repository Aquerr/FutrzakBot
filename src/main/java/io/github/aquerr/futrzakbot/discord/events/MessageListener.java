package io.github.aquerr.futrzakbot.discord.events;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.command.CommandManager;
import io.github.aquerr.futrzakbot.discord.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class MessageListener extends ListenerAdapter
{
    private final FutrzakBot futrzakBot;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;

    public MessageListener(FutrzakBot futrzakBot, FutrzakMessageEmbedFactory messageEmbedFactory)
    {
        this.futrzakBot = futrzakBot;
        this.messageEmbedFactory = messageEmbedFactory;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (isBot(event.getAuthor().getIdLong()))
            return;

        TextChannel textChannel = event.getTextChannel();
        Member member = event.getMember();

        // Bot cannot be used from webhooks and private channels
        if (member == null)
            return;

        if ("!f debugip".equals(event.getMessage().getContentDisplay()) && event.getAuthor().getIdLong() == 272461089541718017L)
        {
            printDebugIp(event);
        }

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

    private void printDebugIp(MessageReceivedEvent event)
    {
        event.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(getPublicIp())).queue();
    }


    /**
     * Used for pagination in help command
     */
    @Override
    public void onGenericGuildMessageReaction(@NotNull GenericGuildMessageReactionEvent event)
    {
        // If it is Futrzak who added reaction... don't process further.
        if (isBot(event.getUserIdLong()))
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

                MessageEmbed newMessage = messageEmbedFactory.createHelpMessage(this.futrzakBot.getCommandManager().getCommands().values(), page - 1);
                message.editMessageEmbeds(newMessage).queue();
            }
            else if (emoji.equals(EmojiUnicodes.ARROW_RIGHT))
            {
                // Get current page and move forward
                int page = getCurrentHelpPage(messageEmbed);

                MessageEmbed newMessage = messageEmbedFactory.createHelpMessage(this.futrzakBot.getCommandManager().getCommands().values(), page + 1);
                message.editMessageEmbeds(newMessage).queue();
            }
        }
    }

    private boolean isMessageWithFutrzakPrefix(String message)
    {
        if ("!f".equals(message))
            return true;

        String[] words = message.split(" ");
        return words.length > 1 && words[0].equals(CommandManager.COMMAND_PREFIX);
    }

    private boolean isFutrzakHelpMessageReaction(MessageEmbed messageEmbed)
    {
        return Optional.ofNullable(messageEmbed.getTitle()).orElse("")
                .equals(futrzakBot.getMessageSource().getMessage("embed.command-list"));
    }

    private int getCurrentHelpPage(MessageEmbed messageEmbed)
    {
        String footer = Optional.ofNullable(messageEmbed.getFooter())
                .map(MessageEmbed.Footer::getText)
                .orElse("");

        return Integer.parseInt(footer.split("\\/")[0]);
    }

    private boolean isBot(final long userId)
    {
        return this.futrzakBot.getJda().getSelfUser().getIdLong() == userId;
    }

    private String getPublicIp()
    {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create("https://api.ipify.org")).build();
        try
        {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String publicIp = httpResponse.body();
            if (publicIp == null || publicIp.isBlank())
                return "Could not get public ip.";

            return publicIp;
        }
        catch (IOException | InterruptedException e)
        {
            return "Could not get public ip: " + e.getMessage();
        }
    }
}
