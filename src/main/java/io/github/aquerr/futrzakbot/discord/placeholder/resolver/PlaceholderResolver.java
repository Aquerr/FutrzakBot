package io.github.aquerr.futrzakbot.discord.placeholder.resolver;

import io.github.aquerr.futrzakbot.discord.placeholder.PlaceholderContext;

public interface PlaceholderResolver
{
    String resolve(PlaceholderContext placeholderContext);
}
