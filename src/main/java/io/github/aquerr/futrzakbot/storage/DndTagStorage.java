package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.model.DndTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DndTagStorage extends JpaRepository<DndTag, Long>
{

}
