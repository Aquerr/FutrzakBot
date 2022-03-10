package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ParameterImpl<T> implements Parameter<T>
{
    String key;
    Class<T> type;
}