package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.discord.games.dnd.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagStorage extends JpaRepository<Tag, Long>
{

}
