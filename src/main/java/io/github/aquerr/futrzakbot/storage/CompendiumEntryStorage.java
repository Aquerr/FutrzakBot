package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntry;
import io.github.aquerr.futrzakbot.discord.games.dnd.CompendiumEntryImpl;
import io.github.aquerr.futrzakbot.discord.games.dnd.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CompendiumEntryStorage extends JpaRepository<CompendiumEntryImpl, Long>, CompendiumEntryWithDetailsStorage
{
    @Query("SELECT compendium_entry FROM CompendiumEntryImpl compendium_entry WHERE compendium_entry.name = :name " +
            "AND :tags IN (compendium_entry.tags)")
    List<CompendiumEntry> findAllByTagsAndNameLike(Set<Tag> tags, String name);

    CompendiumEntry findByName(String name);
}
