package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class DoubleParameter implements Parameter<Double>
{
    @NonNull
    String key;

    @Override
    public Class<Double> getType()
    {
        return Double.class;
    }
}
