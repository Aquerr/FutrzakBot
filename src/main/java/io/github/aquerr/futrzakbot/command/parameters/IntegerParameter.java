package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode
public class IntegerParameter implements Parameter<Integer>
{
    @NonNull
    String key;

    boolean optional;

    @Override
    public Class<Integer> getType()
    {
        return Integer.class;
    }
}
