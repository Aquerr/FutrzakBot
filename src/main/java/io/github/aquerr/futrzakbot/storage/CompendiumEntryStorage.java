package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CompendiumEntryStorage extends JpaRepository<CompendiumEntryImpl, Long>, CompendiumEntryWithDetailsStorage
{
    @Query("FROM CompendiumEntryImpl compendium_entry " +
            "JOIN compendium_entry.tags tags " +
            "WHERE compendium_entry.name = :name " +
            "AND :tags IN (tags)")
    List<CompendiumEntry> findAllByTagsAndNameLike(Set<DndTag> tags, String name);

    CompendiumEntry findByName(String name);
}
