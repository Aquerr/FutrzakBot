package io.github.aquerr.futrzakbot.placeholder.resolver;

import io.github.aquerr.futrzakbot.placeholder.PlaceholderContext;
import io.github.aquerr.futrzakbot.placeholder.Placeholders;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.aquerr.futrzakbot.placeholder.PlaceholderService.*;

public class RandomMemberPlaceholderResolver implements PlaceholderResolver
{
    private static final RandomMemberPlaceholderResolver INSTANCE = new RandomMemberPlaceholderResolver();
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final Placeholders RANDOM_MEMBER_PLACEHOLDER = Placeholders.RANDOM_MEMBER;

    public static RandomMemberPlaceholderResolver getInstance()
    {
        return INSTANCE;
    }

    private RandomMemberPlaceholderResolver()
    {

    }

    @Override
    public String resolve(PlaceholderContext placeholderContext)
    {
        if (!placeholderContext.getText().contains(PLACEHOLDER_START_CHAR + RANDOM_MEMBER_PLACEHOLDER.getPlaceholder() + PLACEHOLDER_END_CHAR))
            return placeholderContext.getText();

        Member member = getRandomMember(placeholderContext.getTextChannel().getGuild());
        return placeholderContext.getText().replaceAll(PLACEHOLDER_START_CHAR_REGEX + RANDOM_MEMBER_PLACEHOLDER.getPlaceholder() + PLACEHOLDER_END_CHAR_REGEX,
                member.getAsMention());
    }

    Member getRandomMember(Guild guild)
    {
        List<Member> members = guild.getMembers();
        int randomMemberIndex = RANDOM.nextInt(members.size());
        return members.get(randomMemberIndex);
    }
}