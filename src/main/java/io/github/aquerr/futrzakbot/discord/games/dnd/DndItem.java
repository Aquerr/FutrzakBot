package io.github.aquerr.futrzakbot.discord.games.dnd;

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
@Table(name = "dnd_item")
@Data
public class DndItem implements CompendiumEntry
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
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
    public String getDescription()
    {
        return this.compendiumEntry.getDescription();
    }

    @Override
    public Set<Tag> getTags()
    {
        return this.compendiumEntry.getTags();
    }

    @Override
    public EntryType getEntryType()
    {
        return EntryType.ITEM;
    }
}
