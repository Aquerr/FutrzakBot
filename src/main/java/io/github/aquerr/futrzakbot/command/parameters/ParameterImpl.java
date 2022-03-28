package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class ParameterImpl<T> implements Parameter<T>
{
    @NonNull
    String key;
    @NonNull
    Class<T> type;
}