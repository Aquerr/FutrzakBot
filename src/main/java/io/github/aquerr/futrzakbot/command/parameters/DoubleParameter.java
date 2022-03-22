package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DoubleParameter implements Parameter<Double>
{
    String key;

    @Override
    public Class<Double> getType()
    {
        return Double.class;
    }
}
