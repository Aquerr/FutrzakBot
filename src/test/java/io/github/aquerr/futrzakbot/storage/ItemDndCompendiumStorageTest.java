package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.DndItem;
import io.github.aquerr.futrzakbot.discord.games.dnd.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemDndCompendiumStorageTest
{
    @Autowired
    private DndItemStorage itemCompendiumStorage;
    @Autowired
    private CompendiumEntryStorage compendiumEntryStorage;

    @Test
    void testSaveDndItem()
    {
        CompendiumEntryImpl compendiumEntry = new CompendiumEntryImpl();
        compendiumEntry.setName("mojItem");
        compendiumEntry.setDescription("moj opis bla bla bla");
        compendiumEntry.setTags(Set.of(new Tag(null, "mojTag"), new Tag(null, "innyTag")));
        compendiumEntryStorage.save(compendiumEntry);

        DndItem dndItem = new DndItem();
        dndItem.setCompendiumEntry(compendiumEntry);

        itemCompendiumStorage.save(dndItem);

        assertThat(itemCompendiumStorage.findAll()).isNotEmpty();
    }

    @Test
    void testFindDndItem()
    {
        CompendiumEntryImpl compendiumEntry = new CompendiumEntryImpl();
        compendiumEntry.setName("mojItem");
        compendiumEntry.setDescription("moj opis bla bla bla");
        compendiumEntry.setTags(Set.of(new Tag(null, "mojTag"), new Tag(null, "innyTag")));
        compendiumEntryStorage.save(compendiumEntry);

        DndItem dndItem = new DndItem();
        dndItem.setCompendiumEntry(compendiumEntry);

        itemCompendiumStorage.save(dndItem);


        CompendiumEntry result = compendiumEntryStorage.findWithDetailsByName("mojItem");
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(DndItem.class);
        assertThat(result.getTags()).isNotEmpty();
    }
}
