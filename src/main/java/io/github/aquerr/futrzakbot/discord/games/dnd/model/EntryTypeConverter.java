package io.github.aquerr.futrzakbot.discord.games.dnd.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EntryTypeConverter implements AttributeConverter<CompendiumEntry.EntryType, Long>
{
    @Override
    public Long convertToDatabaseColumn(CompendiumEntry.EntryType attribute)
    {
        return attribute.getId();
    }

    @Override
    public CompendiumEntry.EntryType convertToEntityAttribute(Long dbData)
    {
        CompendiumEntry.EntryType entryType = CompendiumEntry.EntryType.findById(dbData);
        if (entryType == null)
        {
            throw new IllegalArgumentException("Could not find EntryType for given id = " + dbData);
        }
        return entryType;
    }
}
