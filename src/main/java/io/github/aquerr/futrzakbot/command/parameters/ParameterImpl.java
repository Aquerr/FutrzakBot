package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Builder
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