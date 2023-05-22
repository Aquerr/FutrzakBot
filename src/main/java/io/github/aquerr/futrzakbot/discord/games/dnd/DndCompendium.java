package io.github.aquerr.futrzakbot.discord.games.dnd;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndCreature;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndItem;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndTag;
import io.github.aquerr.futrzakbot.storage.CompendiumEntryStorage;
import io.github.aquerr.futrzakbot.storage.DndItemStorage;
import io.github.aquerr.futrzakbot.storage.entity.DndCreatureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DndCompendium
{
    private final CompendiumEntryStorage compendiumEntryStorage;
    private final DndItemStorage dndItemStorage;
    private final DndCreatureStorage dndCreatureStorage;

    public List<CompendiumEntry> findEntries(Set<DndTag> tags, String searchPhrase)
    {
        return compendiumEntryStorage.findAllByTagsAndNameLike(tags, searchPhrase);
    }

    public Optional<CompendiumEntry> getEntry(String name)
    {
        return compendiumEntryStorage.findWithDetailsByName(name);
    }

    public void save(CompendiumEntry compendiumEntry)
    {
        if (compendiumEntry instanceof CompendiumEntryImpl)
            throw new IllegalArgumentException("Compendium entry must be of type item or creature!");

        if (compendiumEntry instanceof DndItem dndItem)
        {
            dndItemStorage.save(dndItem);
        }
        else if (compendiumEntry instanceof DndCreature dndCreature)
        {
            dndCreatureStorage.save(dndCreature);
        }
    }
}
