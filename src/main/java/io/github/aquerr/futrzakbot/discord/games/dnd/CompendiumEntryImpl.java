package io.github.aquerr.futrzakbot.discord.games.dnd;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "dnd_compendium_entry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompendiumEntryImpl implements CompendiumEntry
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Lob
    @Column(name = "description", nullable = true, unique = false)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "entry_type", nullable = false, unique = false)
    private EntryType entryType;

    @ManyToMany
    @JoinTable(name = "dnd_compendium_entry_tag",
            joinColumns = @JoinColumn(name = "compoendium_entry_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<Tag> tags;
}
