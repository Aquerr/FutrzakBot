package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.storage.entity.MiniGameEncounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiniGameEncounterStorage extends JpaRepository<MiniGameEncounter, Long>
{

}
