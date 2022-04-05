package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;

@Getter
@Builder
@EqualsAndHashCode
public class MemberParameter implements Parameter<Member>
{
    @NonNull
    String key;

    boolean optional;

    @Override
    public Class<Member> getType()
    {
        return Member.class;
    }
}
