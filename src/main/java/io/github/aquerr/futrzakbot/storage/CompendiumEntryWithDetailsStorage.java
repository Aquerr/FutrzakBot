package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntry;

public interface CompendiumEntryWithDetailsStorage
{
    CompendiumEntry findWithDetailsByName(String name);
}
