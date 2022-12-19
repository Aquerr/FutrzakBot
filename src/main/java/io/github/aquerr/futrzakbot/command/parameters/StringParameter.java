package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
public class StringParameter implements Parameter<String>
{
    @NonNull
    String key;

    @Builder.Default
    @NonNull
    Class<String> type = String.class;

    boolean optional;
}
