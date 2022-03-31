package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class StringParameter implements Parameter<String>
{
    @NonNull
    String key;

    @Override
    public Class<String> getType()
    {
        return String.class;
    }
}
