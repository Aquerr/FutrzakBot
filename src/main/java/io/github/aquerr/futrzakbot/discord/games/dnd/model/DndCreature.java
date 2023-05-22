package io.github.aquerr.futrzakbot.discord.games.dnd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "dnd_creature")
@Data
public class DndCreature implements CompendiumEntry
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "compendium_entry_id", unique = true, nullable = false, updatable = false)
    private CompendiumEntryImpl compendiumEntry;

    @Override
    public String getName()
    {
        return this.compendiumEntry.getName();
    }

    @Override
    public EntryType getEntryType()
    {
        return EntryType.CREATURE;
    }

    @Override
    public String getDescription()
    {
        return this.compendiumEntry.getDescription();
    }

    @Override
    public Set<DndTag> getTags()
    {
        return this.compendiumEntry.getTags();
    }
}
