package io.github.aquerr.futrzakbot.discord.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode
public class IntegerParameter implements Parameter<Integer>
{
    @NonNull
    String key;
    boolean optional;

    @Builder.Default
    @NonNull
    Class<Integer> type = Integer.class;
}
