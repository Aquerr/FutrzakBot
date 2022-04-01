package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class IntegerParameter implements Parameter<Integer>
{
    @NonNull
    String key;

    @Override
    public Class<Integer> getType()
    {
        return Integer.class;
    }
}
