package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RemainingStringsParameter implements Parameter<String>
{
    String key;

    @Override
    public Class<String> getType()
    {
        return String.class;
    }
}
