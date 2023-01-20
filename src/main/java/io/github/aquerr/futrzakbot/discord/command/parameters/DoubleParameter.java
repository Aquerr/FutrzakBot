package io.github.aquerr.futrzakbot.discord.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@EqualsAndHashCode
public class DoubleParameter implements Parameter<Double>
{
    @NonNull
    String key;

    @lombok.Builder.Default
    @NonNull
    Class<Double> type = Double.class;

    boolean optional;
}
