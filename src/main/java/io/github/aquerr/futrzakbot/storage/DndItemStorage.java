package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.DndItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DndItemStorage extends JpaRepository<DndItem, Long>
{
    List<DndItem> findAllByCompendiumEntry_NameLike(String name);
}
