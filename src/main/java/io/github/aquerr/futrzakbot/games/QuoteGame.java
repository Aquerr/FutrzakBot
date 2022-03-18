package io.github.aquerr.futrzakbot.games;

import io.github.aquerr.futrzakbot.placeholder.PlaceholderContext;
import io.github.aquerr.futrzakbot.placeholder.PlaceholderService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteGame
{
    private static final String QUOTES_FILE_NAME = "quotes.txt";
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final QuoteGame INSTANCE = new QuoteGame();

    private final PlaceholderService placeholderService = PlaceholderService.getInstance();

    private QuoteGame() {}

    public static QuoteGame getInstance()
    {
        return INSTANCE;
    }

    public void printRandomQuote(TextChannel textChannel, Member member)
    {
        String randomQuote = getRandomQuote();
        String processedRandomQuote = processPlaceholders(randomQuote, textChannel, member);

        textChannel.sendMessage(processedRandomQuote).queue();
    }

    private String processPlaceholders(String randomQuote, TextChannel textChannel, Member member)
    {
        return placeholderService.processPlaceholders(new PlaceholderContext(randomQuote, textChannel, member));
    }

    private List<String> getAllQuotesFromFile() throws IOException
    {
        createQuotesFileIfNotExist();
        return Files.readAllLines(Paths.get(QUOTES_FILE_NAME),StandardCharsets.UTF_8);
    }

    private void createQuotesFileIfNotExist() throws IOException
    {
        File quotes = new File(QUOTES_FILE_NAME);
        if (!quotes.exists())
        {
            quotes.createNewFile();
        }
    }

    private String getRandomQuote()
    {
        List<String> quotes = Collections.emptyList();

        try
        {
            quotes = getAllQuotesFromFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (quotes.isEmpty())
        {
            return "Brak cytatów!";
        }

        int randQuote = RANDOM.nextInt(quotes.size());
        return quotes.get(randQuote);
    }
}
