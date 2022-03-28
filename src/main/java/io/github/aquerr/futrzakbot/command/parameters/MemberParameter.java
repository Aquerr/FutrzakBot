package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;

@Getter
@Builder
public class MemberParameter implements Parameter<Member>
{
    @NonNull
    String key;

    @Override
    public Class<Member> getType()
    {
        return Member.class;
    }
}
