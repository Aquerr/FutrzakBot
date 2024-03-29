package io.github.aquerr.futrzakbot.discord.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@EqualsAndHashCode
public class RemainingStringsParameter implements Parameter<String>
{
    @NonNull
    String key;

    boolean optional;

    @NonNull
    @Builder.Default
    Class<String> type = String.class;
}
