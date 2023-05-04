package io.github.aquerr.futrzakbot.discord.games.dnd;

import java.util.Set;

public interface CompendiumEntry
{
    String getName();

    String getDescription();

    Set<Tag> getTags();

    EntryType getEntryType();

    enum EntryType
    {
        ITEM,
        CREATURE;
    }
}
