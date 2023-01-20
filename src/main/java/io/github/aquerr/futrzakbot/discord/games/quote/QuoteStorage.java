package io.github.aquerr.futrzakbot.discord.games.quote;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class QuoteStorage
{
    private static QuoteStorage INSTANCE;
    private static final String QUOTES_FILE_NAME = "quotes.json";

    private static final TypeRef<List<QuoteCategory>> QUOTE_CATEGORY_LIST_TYPE_REF = new TypeRef<>() {};

    public static QuoteStorage getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new QuoteStorage();
        return INSTANCE;
    }

    private QuoteStorage()
    {
        try
        {
            createQuotesFileIfNotExist();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<QuoteCategory> getQuoteCategories() throws IOException
    {
        DocumentContext documentContext = getQuotesJson();
        List<QuoteCategory> categories = documentContext.read("$.categories", QUOTE_CATEGORY_LIST_TYPE_REF);
        return categories;
    }

    private DocumentContext getQuotesJson() throws IOException
    {
        String quotesFileAsString = Files.readString(Paths.get(QUOTES_FILE_NAME), StandardCharsets.UTF_8);
        DocumentContext quotesDocumentContext = JsonPath.parse(quotesFileAsString);
        return quotesDocumentContext;
    }

    private void createQuotesFileIfNotExist() throws IOException
    {
        File quotes = new File(QUOTES_FILE_NAME);
        if (!quotes.exists())
        {
            quotes.createNewFile();
        }
    }
}
