package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.StringParameter;
import io.github.aquerr.futrzakbot.discord.games.quote.QuoteCategory;
import io.github.aquerr.futrzakbot.discord.games.quote.QuoteGame;
import io.github.aquerr.futrzakbot.discord.games.quote.exception.QuoteCategoryNotFound;
import io.github.aquerr.futrzakbot.discord.games.quote.exception.QuotesNotFoundException;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
public class QuoteCommand implements Command, SlashCommand
{
    private static final String HELP_PARAM_KEY = "help";
    private static final String RANDOM_PARAM_KEY = "random";
    private static final String CATEGORY_PARAM_KEY = "category";

    private static final String AVAILABLE_CATEGORIES = "command.quote.categories.available";
    private static final String QUOTE_CATEGORY_NOT_FOUND = "command.quote.category.not_found";
    private static final String QUOTES_NOT_FOUND = "command.quote.quotes.not_found";
    private static final String QUOTE_COMMAND_CATEGORIES_HELP = "command.quote.categories.help";

    private final QuoteGame quoteGame;
    private final MessageSource messageSource;

    public QuoteCommand(QuoteGame quoteGame, MessageSource messageSource)
    {
        this.quoteGame = quoteGame;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context) throws CommandException
    {
        String category = context.<String>get(CATEGORY_PARAM_KEY).orElse("");
        GuildMessageChannel channel = context.getGuildMessageChannel();
        Member member = context.getMember();

        if ("?".equals(category))
        {
            // print help + possible categories
            channel.sendMessageEmbeds(getHelpMessage()).queue();
        }
        else if ("".equals(category))
        {
            // print quote from random category
            try
            {
                channel.sendMessageEmbeds(this.quoteGame.getRandomQuote(channel, member)).queue();
            }
            catch (QuotesNotFoundException exception)
            {
                log.warn(exception.getMessage(), exception);
                throw new CommandException(messageSource.getMessage(QUOTES_NOT_FOUND));
            }
            catch (IOException exception)
            {
                log.error(exception.getMessage(), exception);
                throw new CommandException(exception.getMessage());
            }
        }
        else
        {
            // print quote from selected category
            try
            {
                channel.sendMessageEmbeds(this.quoteGame.getRandomQuoteFromCategory(channel, member, category)).queue();
            }
            catch (QuoteCategoryNotFound e)
            {
                log.warn(e.getMessage(), e);
                throw new CommandException(messageSource.getMessage(QUOTE_CATEGORY_NOT_FOUND));
            }
            catch (QuotesNotFoundException e)
            {
                log.warn(e.getMessage(), e);
                throw new CommandException(messageSource.getMessage(QUOTES_NOT_FOUND));
            }
            catch (IOException e)
            {
                log.error(e.getMessage(), e);
                throw new CommandException(e.getMessage());
            }
        }
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("quote", "cytat");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.quote.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.quote.description");
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData()
                .addOption(OptionType.BOOLEAN, HELP_PARAM_KEY, messageSource.getMessage("command.quote.slash.param.help.desc"), false)
                .addOption(OptionType.BOOLEAN, RANDOM_PARAM_KEY, messageSource.getMessage("command.quote.slash.param.random.desc"), false)
                .addOption(OptionType.STRING, CATEGORY_PARAM_KEY, messageSource.getMessage("command.quote.slash.param.category.desc"), false);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        MessageChannel messageChannel = event.getChannel().asGuildMessageChannel();
        Member member = event.getMember();
        boolean shouldShowHelp = ofNullable(event.getOption(HELP_PARAM_KEY)).map(OptionMapping::getAsBoolean).orElse(false);
        if (shouldShowHelp)
        {
            event.replyEmbeds(getHelpMessage()).queue();
            return;
        }

        boolean randomQuote = ofNullable(event.getOption(RANDOM_PARAM_KEY)).map(OptionMapping::getAsBoolean).orElse(false);
        if (randomQuote)
        {
            // print quote from random category
            try
            {
                MessageEmbed messageEmbed = this.quoteGame.getRandomQuote(messageChannel, member);
                event.replyEmbeds(messageEmbed).queue();
            }
            catch (QuotesNotFoundException exception)
            {
                log.warn(exception.getMessage(), exception);
                event.reply(messageSource.getMessage(QUOTES_NOT_FOUND)).queue();
            }
            catch (IOException exception)
            {
                log.error(exception.getMessage(), exception);
                event.reply(exception.getMessage()).queue();
            }
            return;
        }

        String category = ofNullable(event.getOption(CATEGORY_PARAM_KEY)).map(OptionMapping::getAsString).orElse(null);
        if (category == null)
        {
            event.replyEmbeds(getHelpMessage()).queue();
            return;
        }

        // print quote from random category
        try
        {
            MessageEmbed messageEmbed = this.quoteGame.getRandomQuoteFromCategory(messageChannel, member, category);
            event.replyEmbeds(messageEmbed).queue();
        }
        catch (QuotesNotFoundException exception)
        {
            log.warn(exception.getMessage(), exception);
            event.reply(messageSource.getMessage(QUOTES_NOT_FOUND)).queue();
        }
        catch (QuoteCategoryNotFound e)
        {
            log.warn(e.getMessage(), e);
            event.reply(messageSource.getMessage(QUOTE_CATEGORY_NOT_FOUND)).queue();
        }
        catch (IOException exception)
        {
            log.error(exception.getMessage(), exception);
            event.reply(exception.getMessage()).queue();
        }
        return;
    }

    @Override
    public String getUsage()
    {
        return Command.super.getUsage() + "\n" + messageSource.getMessage(QUOTE_COMMAND_CATEGORIES_HELP);
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(StringParameter.builder().key(CATEGORY_PARAM_KEY).optional(true).build());
    }

    private MessageEmbed getHelpMessage()
    {
        List<QuoteCategory> categories = this.quoteGame.getAvailableCategories();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GREEN);
        embedBuilder.setTitle(messageSource.getMessage(AVAILABLE_CATEGORIES));

        for (final QuoteCategory quoteCategory : categories)
        {
            embedBuilder.addField(quoteCategory.getName(), String.join(", ", quoteCategory.getAliases()), false);
        }
        return embedBuilder.build();
    }
}
