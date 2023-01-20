package io.github.aquerr.futrzakbot.discord.command.parameters;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode
public class ParameterImpl<T> implements Parameter<T>
{
    @NonNull
    String key;
    @NonNull
    Class<T> type;

    boolean optional;
}