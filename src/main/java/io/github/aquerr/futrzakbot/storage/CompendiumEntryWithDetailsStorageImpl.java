package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndCreature;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class CompendiumEntryWithDetailsStorageImpl implements CompendiumEntryWithDetailsStorage
{

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<CompendiumEntry> findWithDetailsByName(String name)
    {
        // Find compendium entry
        TypedQuery<CompendiumEntryImpl> compendiumEntryTypedQuery = entityManager.createQuery("SELECT compendium_entry FROM CompendiumEntryImpl compendium_entry WHERE compendium_entry.name = :name", CompendiumEntryImpl.class);
        compendiumEntryTypedQuery.setParameter("name", name);

        CompendiumEntryImpl compendiumEntry = compendiumEntryTypedQuery.getResultStream().findFirst().orElse(null);

        // Find details for item or creature
        CompendiumEntry result = null;
        if (compendiumEntry != null)
        {
            if (compendiumEntry.getEntryType() == CompendiumEntry.EntryType.ITEM)
            {
                result = findDndItemByCompendiumEntryId(compendiumEntry.getId());
            }
            else if (compendiumEntry.getEntryType() == CompendiumEntry.EntryType.CREATURE)
            {
                result = findDndCreatureByCompendiumEntryId(compendiumEntry.getId());
            }
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<DndItem> findDndItemByName(String name)
    {
        CompendiumEntry compendiumEntry = findWithDetailsByName(name).orElse(null);
        if (compendiumEntry == null)
        {
            return Optional.empty();
        }
        else if (compendiumEntry.getEntryType() != CompendiumEntry.EntryType.ITEM)
        {
            throw new ClassCastException("Could not cast incompatible object to " + DndItem.class.getSimpleName());
        }
        return Optional.of((DndItem) compendiumEntry);
    }

    @Override
    public Optional<DndCreature> findDndCreatureByName(String name)
    {
        CompendiumEntry compendiumEntry = findWithDetailsByName(name).orElse(null);
        if (compendiumEntry == null)
        {
            return Optional.empty();
        }
        else if (compendiumEntry.getEntryType() != CompendiumEntry.EntryType.CREATURE)
        {
            throw new ClassCastException("Could not cast incompatible object to " + DndCreature.class.getSimpleName());
        }
        return Optional.of((DndCreature) compendiumEntry);
    }

    private DndItem findDndItemByCompendiumEntryId(Long compendiumEntryId)
    {
        TypedQuery<DndItem> typedQuery = entityManager.createQuery("SELECT dnd_item FROM DndItem dnd_item WHERE dnd_item.compendiumEntry.id = :compendium_entry_id", DndItem.class);
        typedQuery.setParameter("compendium_entry_id", compendiumEntryId);
        return typedQuery.getResultStream().findFirst().orElse(null);
    }

    private DndCreature findDndCreatureByCompendiumEntryId(Long compendiumEntryId)
    {
        TypedQuery<DndCreature> typedQuery = entityManager.createQuery("SELECT dnd_creature FROM DndCreature dnd_creature WHERE dnd_creature.compendiumEntry.id = :compendium_entry_id", DndCreature.class);
        typedQuery.setParameter("compendium_entry_id", compendiumEntryId);
        return typedQuery.getResultStream().findFirst().orElse(null);
    }
}
