package io.github.aquerr.futrzakbot.placeholder.resolver;

import io.github.aquerr.futrzakbot.placeholder.PlaceholderContext;

public interface PlaceholderResolver
{
    String resolve(PlaceholderContext placeholderContext);
}
