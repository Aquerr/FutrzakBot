package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntParameter implements Parameter<Integer>
{
    String key;

    @Override
    public Class<Integer> getType()
    {
        return Integer.class;
    }
}
