package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndCreature;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndItem;

import java.util.Optional;

public interface CompendiumEntryWithDetailsStorage
{
    Optional<CompendiumEntry> findWithDetailsByName(String name);

    Optional<DndItem> findDndItemByName(String name);

    Optional<DndCreature> findDndCreatureByName(String name);
}
