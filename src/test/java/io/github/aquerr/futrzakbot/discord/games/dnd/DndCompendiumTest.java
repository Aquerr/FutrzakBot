package io.github.aquerr.futrzakbot.discord.games.dnd;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndCreature;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndItem;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DndCompendium.class)
class DndCompendiumTest
{
    @Autowired
    private DndCompendium dndCompendium;

    @Test
    void testSaveDndItem()
    {
        CompendiumEntryImpl compendiumEntry = new CompendiumEntryImpl();
        compendiumEntry.setName("mojItem");
        compendiumEntry.setDescription("moj opis bla bla bla");
        compendiumEntry.setEntryType(CompendiumEntry.EntryType.ITEM);
        compendiumEntry.setTags(Set.of(new DndTag(null, "mojTag"), new DndTag(null, "innyTag")));
        DndItem dndItem = new DndItem();
        dndItem.setCompendiumEntry(compendiumEntry);

        dndCompendium.save(dndItem);

        assertThat(dndCompendium.getEntry("mojItem")).isNotEmpty();
    }

    @Test
    void testSaveDndCreature()
    {
        CompendiumEntryImpl compendiumEntry = new CompendiumEntryImpl();
        compendiumEntry.setName("mojCreature");
        compendiumEntry.setDescription("moj opis bla bla bla");
        compendiumEntry.setEntryType(CompendiumEntry.EntryType.CREATURE);
        compendiumEntry.setTags(Set.of(new DndTag(null, "mojTag"), new DndTag(null, "innyTag")));
        DndCreature dndCreature = new DndCreature();
        dndCreature.setCompendiumEntry(compendiumEntry);

        dndCompendium.save(dndCreature);

        assertThat(dndCompendium.getEntry("mojCreature")).isNotEmpty();
    }
}