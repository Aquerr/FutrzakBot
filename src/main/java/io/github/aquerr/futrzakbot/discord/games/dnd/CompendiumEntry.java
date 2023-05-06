package io.github.aquerr.futrzakbot.discord.games.dnd;

import java.util.Set;
import java.util.stream.Stream;

public interface CompendiumEntry
{
    String getName();

    String getDescription();

    Set<DndTag> getTags();

    EntryType getEntryType();

    enum EntryType
    {
        ITEM(1, "ITEM"),
        CREATURE(2, "CREATURE");

        private final long id;
        private final String entryTypeName;

        EntryType(int id, String entryTypeName)
        {
            this.id = id;
            this.entryTypeName = entryTypeName;
        }

        public long getId()
        {
            return id;
        }

        public String getEntryTypeName()
        {
            return entryTypeName;
        }

        public static EntryType findById(final Long id)
        {
            return Stream.of(EntryType.values())
                    .filter(entryType -> entryType.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
    }
}
