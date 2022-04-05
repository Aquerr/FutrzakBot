package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.exception.CommandException;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.StringParameter;
import io.github.aquerr.futrzakbot.games.quote.QuoteCategory;
import io.github.aquerr.futrzakbot.games.quote.QuoteGame;
import io.github.aquerr.futrzakbot.games.quote.exception.QuoteCategoryNotFound;
import io.github.aquerr.futrzakbot.games.quote.exception.QuotesNotFoundException;
import io.github.aquerr.futrzakbot.message.MessageSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuoteCommand implements Command
{
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteCommand.class);

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
        TextChannel textChannel = context.getTextChannel();
        Member member = context.getMember();

        if ("?".equals(category))
        {
            // print help + possible categories
            List<QuoteCategory> categories = this.quoteGame.getAvailableCategories();
            printAvailableCategories(textChannel, categories);
        }
        else if ("".equals(category))
        {
            // print quote from random category
            try
            {
                this.quoteGame.printRandomQuote(textChannel, member);
            }
            catch (QuotesNotFoundException exception)
            {
                LOGGER.warn(exception.getMessage(), exception);
                throw new CommandException(messageSource.getMessage(QUOTES_NOT_FOUND));
            }
            catch (IOException exception)
            {
                LOGGER.error(exception.getMessage(), exception);
                throw new CommandException(exception.getMessage());
            }
        }
        else
        {
            // print quote from selected category
            try
            {
                this.quoteGame.printRandomQuoteFromCategory(textChannel, member, category);
            }
            catch (QuoteCategoryNotFound e)
            {
                LOGGER.error(e.getMessage(), e);
                throw new CommandException(messageSource.getMessage(QUOTE_CATEGORY_NOT_FOUND));
            }
            catch (QuotesNotFoundException e)
            {
                LOGGER.error(e.getMessage(), e);
                throw new CommandException(messageSource.getMessage(QUOTES_NOT_FOUND));
            }
            catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
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
        return ":thought_balloon: Cytat: ";
    }

    @Override
    public String getDescription()
    {
        return "Wylosuj cytat";
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

    private void printAvailableCategories(TextChannel textChannel, List<QuoteCategory> categories)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GREEN);
        embedBuilder.setTitle(messageSource.getMessage(AVAILABLE_CATEGORIES));

        for (final QuoteCategory quoteCategory : categories)
        {
            embedBuilder.addField(quoteCategory.getName(), String.join(", ", quoteCategory.getAliases()), false);
        }
        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
