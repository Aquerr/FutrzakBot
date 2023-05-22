package io.github.aquerr.futrzakbot.storage.entity;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndCreature;
import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DndCreatureStorage extends JpaRepository<DndCreature, Long>
{
    List<DndItem> findAllByCompendiumEntry_NameLike(String name);
}
