package io.github.aquerr.futrzakbot.placeholder;

import io.github.aquerr.futrzakbot.placeholder.resolver.PlaceholderResolver;
import io.github.aquerr.futrzakbot.placeholder.resolver.RandomMemberPlaceholderResolver;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderService
{
    public static final String PLACEHOLDER_START_CHAR = "{";
    public static final String PLACEHOLDER_END_CHAR = "}";
    public static final String PLACEHOLDER_START_CHAR_REGEX = "\\" + PLACEHOLDER_START_CHAR;
    public static final String PLACEHOLDER_END_CHAR_REGEX = "\\" + PLACEHOLDER_END_CHAR;

    private static final PlaceholderService INSTANCE = new PlaceholderService();

    private final List<PlaceholderResolver> placeholderResolvers = new ArrayList<>();

    public static PlaceholderService getInstance()
    {
        return INSTANCE;
    }

    private PlaceholderService()
    {
        placeholderResolvers.add(RandomMemberPlaceholderResolver.getInstance());
    }

    public String processPlaceholders(PlaceholderContext context)
    {
        String text = context.getText();
        for (final PlaceholderResolver placeholderResolver : placeholderResolvers)
        {
            text = placeholderResolver.resolve(context);
        }
        return text;
    }
}
