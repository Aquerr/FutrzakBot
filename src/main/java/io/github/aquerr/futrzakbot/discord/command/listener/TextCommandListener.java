package io.github.aquerr.futrzakbot.discord.command.listener;

import io.github.aquerr.futrzakbot.discord.command.CommandManager;
import io.github.aquerr.futrzakbot.discord.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.EnumSet;
import java.util.Optional;

import static io.github.aquerr.futrzakbot.FutrzakBot.COULD_NOT_REGISTER_SLASH_COMMANDS;

public class TextCommandListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TextCommandListener.class);

    private static final EnumSet<ChannelType> SUPPORTED_CHANNEL_TYPES = EnumSet.of(
            ChannelType.TEXT,
            ChannelType.GUILD_PUBLIC_THREAD
    );

    private final CommandManager commandManager;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;
    private final MessageSource messageSource;

    public TextCommandListener(CommandManager commandManager,
                               FutrzakMessageEmbedFactory messageEmbedFactory,
                               MessageSource messageSource)
    {
        this.commandManager = commandManager;
        this.messageEmbedFactory = messageEmbedFactory;
        this.messageSource = messageSource;
    }

    public void onEvent(@NotNull GenericEvent event)
    {
        if (event instanceof MessageReceivedEvent newEvent)
        {
            onMessageReceived(newEvent);
        }
        else if (event instanceof GenericMessageReactionEvent newEvent)
        {
            onGenericMessageReaction(newEvent);
        }
        else if (event instanceof GuildJoinEvent newEvent)
        {
            onGuildJoin(newEvent);
        }
    }

    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromGuild())
        {
            handleGuildMessageEvent(event);
        }
        else
        {
            handlePrivateMessageEvent(event);
        }
    }

    private void handleGuildMessageEvent(MessageReceivedEvent event)
    {
        if (isBot(event, event.getAuthor().getIdLong()))
            return;

        MessageChannelUnion messageChannelUnion = event.getChannel();
        if (!isSupportedChannelType(messageChannelUnion))
            return;

        Member member = event.getMember();

        // Bot cannot be used from webhooks and private channels
        if (member == null)
            return;

        if (isMessageWithFutrzakPrefix(event.getMessage().getContentDisplay()))
        {
            this.commandManager.processTextCommand(member, messageChannelUnion, event.getMessage());
        }

        if(event.getMessage().getContentDisplay().contains("Kocham") || event.getMessage().getContentDisplay().contains("kocham") || event.getMessage().getContentDisplay().contains("lofki")
                || event.getMessage().getContentDisplay().contains("loffciam") || event.getMessage().getContentDisplay().contains("stellar"))
        {
            event.getMessage().addReaction(Emoji.fromUnicode("❤")).queue();
        }
    }

    private boolean isSupportedChannelType(MessageChannel messageChannel)
    {
        return SUPPORTED_CHANNEL_TYPES.contains(messageChannel.getType());
    }

    private void handlePrivateMessageEvent(MessageReceivedEvent event)
    {
        if ("!f debugip".equals(event.getMessage().getContentDisplay()) && event.getAuthor().getIdLong() == 272461089541718017L)
        {
            printDebugIp(event);
        }
    }

    private void printDebugIp(MessageReceivedEvent event)
    {
        event.getChannel().sendMessage(getPublicIp()).queue();
    }


    /**
     * Used for pagination in help command
     */
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event)
    {
        // If it is Futrzak who added reaction... don't process further.
        if (isBot(event, event.getUserIdLong()))
            return;

        Message message = event.retrieveMessage().complete();
        if (message.getEmbeds().isEmpty())
            return;

        MessageEmbed messageEmbed = message.getEmbeds().get(0);

        if (isFutrzakHelpMessageReaction(messageEmbed))
        {
            String emoji = event.getEmoji().asUnicode().getAsCodepoints();
            if (emoji.equals(EmojiUnicodes.ARROW_LEFT))
            {
                // Get current page and move back
                int page = getCurrentHelpPage(messageEmbed);

                MessageEmbed newMessage = messageEmbedFactory.createHelpMessage(this.commandManager.getCommands().values(), page - 1);
                message.editMessageEmbeds(newMessage).queue();
            }
            else if (emoji.equals(EmojiUnicodes.ARROW_RIGHT))
            {
                // Get current page and move forward
                int page = getCurrentHelpPage(messageEmbed);

                MessageEmbed newMessage = messageEmbedFactory.createHelpMessage(this.commandManager.getCommands().values(), page + 1);
                message.editMessageEmbeds(newMessage).queue();
            }
        }
    }

    private boolean isMessageWithFutrzakPrefix(String message)
    {
        if (CommandManager.COMMAND_PREFIX.equals(message))
            return true;

        String[] words = message.split(" ");
        return words.length > 1 && words[0].equals(CommandManager.COMMAND_PREFIX);
    }

    private boolean isFutrzakHelpMessageReaction(MessageEmbed messageEmbed)
    {
        return Optional.ofNullable(messageEmbed.getTitle()).orElse("")
                .equals(messageSource.getMessage("embed.command-list"));
    }

    private int getCurrentHelpPage(MessageEmbed messageEmbed)
    {
        String footer = Optional.ofNullable(messageEmbed.getFooter())
                .map(MessageEmbed.Footer::getText)
                .orElse("");

        return Integer.parseInt(footer.split("\\/")[0]);
    }

    private boolean isBot(final GenericEvent event, final Long userId)
    {
        return event.getJDA().getSelfUser().getIdLong() == userId;
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

    private void onGuildJoin(GuildJoinEvent event)
    {
        tryToRegisterPlayerGuildSlashCommands(event.getGuild());
    }

    private void tryToRegisterPlayerGuildSlashCommands(Guild guild)
    {
        try
        {
            this.commandManager.registerSlashCommandsForGuild(guild);
        }
        catch (Exception exception)
        {
            LOGGER.warn(messageSource.getMessage(COULD_NOT_REGISTER_SLASH_COMMANDS, guild.getName(), exception.getMessage()));
        }
    }
}
