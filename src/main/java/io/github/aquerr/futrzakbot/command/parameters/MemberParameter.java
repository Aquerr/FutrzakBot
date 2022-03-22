package io.github.aquerr.futrzakbot.command.parameters;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

@Getter
@Builder
public class MemberParameter implements Parameter<Member>
{
    String key;

    @Override
    public Class<Member> getType()
    {
        return Member.class;
    }
}
