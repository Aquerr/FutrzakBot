package io.github.aquerr.futrzakbot.discord.games.dnd;

import io.github.aquerr.futrzakbot.storage.CompendiumEntryStorage;
import io.github.aquerr.futrzakbot.storage.DndItemStorage;
import io.github.aquerr.futrzakbot.util.SpringContextHelper;

import java.util.List;
import java.util.Set;

public class DndCompendium
{
    private final CompendiumEntryStorage compendiumEntryStorage;
    public DndCompendium()
    {
        this.compendiumEntryStorage = SpringContextHelper.getBean(CompendiumEntryStorage.class);
    }

    public List<CompendiumEntry> findEntries(Set<Tag> tags, String searchPhrase)
    {
        return compendiumEntryStorage.findAllByTagsAndNameLike(tags, searchPhrase);
    }

    public CompendiumEntry getEntry(String name)
    {
        CompendiumEntry compendiumEntry = compendiumEntryStorage.findWithDetailsByName(name);
        return compendiumEntry;
    }
}
