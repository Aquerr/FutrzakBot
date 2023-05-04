package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.DndCreature;
import io.github.aquerr.futrzakbot.discord.games.dnd.DndItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CompendiumEntryWithDetailsStorageImpl implements CompendiumEntryWithDetailsStorage
{
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public CompendiumEntry findWithDetailsByName(String name)
    {
        // Find compendium entry
        TypedQuery<CompendiumEntryImpl> compendiumEntryTypedQuery = entityManager.createQuery("SELECT compendium_entry FROM CompendiumEntryImpl compendium_entry WHERE compendium_entry.name = :name", CompendiumEntryImpl.class);
        compendiumEntryTypedQuery.setParameter("name", name);

        CompendiumEntryImpl compendiumEntry = compendiumEntryTypedQuery.getSingleResult();

        // Find details for item or creature
        CompendiumEntry result = null;
        if (compendiumEntry.getEntryType() == CompendiumEntry.EntryType.ITEM)
        {
            result = findDndItemByCompendiumEntryId(compendiumEntry.getId());
        }
        else if (compendiumEntry.getEntryType() == CompendiumEntry.EntryType.CREATURE)
        {
            result = findDndCreatureByCompendiumEntryId(compendiumEntry.getId());
        }

        return result;
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
