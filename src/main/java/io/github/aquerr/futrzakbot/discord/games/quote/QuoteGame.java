package io.github.aquerr.futrzakbot.discord.games.quote;

import io.github.aquerr.futrzakbot.discord.games.quote.exception.QuoteCategoryNotFound;
import io.github.aquerr.futrzakbot.discord.games.quote.exception.QuotesNotFoundException;
import io.github.aquerr.futrzakbot.discord.placeholder.PlaceholderContext;
import io.github.aquerr.futrzakbot.discord.placeholder.PlaceholderService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteGame
{
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final QuoteGame INSTANCE = new QuoteGame();

    private final PlaceholderService placeholderService = PlaceholderService.getInstance();
    private final QuoteStorage quoteStorage = QuoteStorage.getInstance();

    private QuoteGame()
    {

    }

    public static QuoteGame getInstance()
    {
        return INSTANCE;
    }

    public MessageEmbed getRandomQuoteFromCategory(MessageChannel messageChannel, Member member, String categoryAlias) throws IOException, QuoteCategoryNotFound, QuotesNotFoundException
    {
        QuoteCategory category = getAvailableCategories().stream()
                .filter(quoteCategory -> quoteCategory.getAliases().contains(categoryAlias))
                .findFirst()
                .orElseThrow(() -> new QuoteCategoryNotFound("Category '" + categoryAlias + "' does not exist!"));
        List<String> quotes = category.getQuotes();
        if (quotes.isEmpty())
            throw new QuotesNotFoundException("Quotes not found for category: " + categoryAlias);

        String randomQuote = getRandomQuote(quotes);
        return prepareQuoteMessage(randomQuote, messageChannel, member, category);
    }

    public MessageEmbed getRandomQuote(MessageChannel messageChannel, Member member) throws QuotesNotFoundException, IOException
    {
        QuoteCategory randomCategory = getRandomCategory();
        List<String> quotes = randomCategory.getQuotes();
        if (quotes.isEmpty())
            throw new QuotesNotFoundException("Quotes not found!");
        String quote = getRandomQuote(quotes);
        return prepareQuoteMessage(quote, messageChannel, member, randomCategory);
    }

    private MessageEmbed prepareQuoteMessage(String quote, MessageChannel messageChannel, Member member, QuoteCategory category)
    {
        String processedRandomQuote = processPlaceholders(quote, messageChannel, member);
        return new EmbedBuilder()
                .setTitle(category.getName())
                .setDescription(processedRandomQuote)
                .setColor(Color.GREEN)
                .build();
    }

    private QuoteCategory getRandomCategory() throws QuotesNotFoundException
    {
        List<QuoteCategory> categories = getAvailableCategories();
        if (categories.isEmpty())
            throw new QuotesNotFoundException("Quotes not found!");
        int randomCategoryIndex = RANDOM.nextInt(categories.size());
        return categories.get(randomCategoryIndex);
    }

    public List<QuoteCategory> getAvailableCategories()
    {
        try
        {
            return quoteStorage.getQuoteCategories();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String processPlaceholders(String randomQuote, MessageChannel messageChannel, Member member)
    {
        return placeholderService.processPlaceholders(new PlaceholderContext(randomQuote, messageChannel, member));
    }

    private String getRandomQuote(List<String> quotes)
    {
        int randQuote = RANDOM.nextInt(quotes.size());
        return quotes.get(randQuote);
    }
}
