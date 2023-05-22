package io.github.aquerr.futrzakbot.discord.games.dnd;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndTag;
import io.github.aquerr.futrzakbot.storage.CompendiumEntryStorage;
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

    public List<CompendiumEntry> findEntries(Set<DndTag> tags, String searchPhrase)
    {
        return compendiumEntryStorage.findAllByTagsAndNameLike(tags, searchPhrase);
    }

    public Optional<CompendiumEntry> getEntry(String name)
    {
        return compendiumEntryStorage.findWithDetailsByName(name);
    }
}
