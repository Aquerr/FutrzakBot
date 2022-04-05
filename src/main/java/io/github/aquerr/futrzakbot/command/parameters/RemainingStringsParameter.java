package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@EqualsAndHashCode
public class RemainingStringsParameter implements Parameter<String>
{
    @NonNull
    String key;

    boolean optional;

    @Override
    public Class<String> getType()
    {
        return String.class;
    }
}
