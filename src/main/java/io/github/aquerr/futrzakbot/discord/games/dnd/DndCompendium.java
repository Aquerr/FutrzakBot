package io.github.aquerr.futrzakbot.discord.games.dnd;

import io.github.aquerr.futrzakbot.storage.CompendiumEntryStorage;
import io.github.aquerr.futrzakbot.util.SpringContextHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DndCompendium
{
    private final CompendiumEntryStorage compendiumEntryStorage;
    public DndCompendium()
    {
        this.compendiumEntryStorage = SpringContextHelper.getBean(CompendiumEntryStorage.class);
    }

    public List<CompendiumEntry> findEntries(Set<DndTag> tags, String searchPhrase)
    {
        return compendiumEntryStorage.findAllByTagsAndNameLike(tags, searchPhrase);
    }

    public Optional<CompendiumEntry> getEntry(String name)
    {
        return compendiumEntryStorage.findWithDetailsByName(name);
    }
}
