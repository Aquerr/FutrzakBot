package io.github.aquerr.futrzakbot.command.parsing.parsers;

import io.github.aquerr.futrzakbot.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.command.parsing.ParsingContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class MemberArgumentParser implements ArgumentParser<Member>
{
    private static final Pattern MENTION_PATTERN = Pattern.compile("^<@!\\d+>$");

    @Override
    public Member parse(ParsingContext context) throws ArgumentParseException
    {
        TextChannel textChannel = context.getTextChannel();
        String argument = context.getArgument();
        return resolveMember(textChannel.getGuild(), argument);
    }

    private boolean isMention(String member)
    {
        return MENTION_PATTERN.matcher(member).matches();
    }

    private String getMemberIdFromMention(String mentionString)
    {
        return mentionString.substring(3, mentionString.length() - 1);
    }

    private Member resolveMember(Guild guild, String argument) throws ArgumentParseException
    {
        if (isMention(argument))
            return resolveMemberFromMention(guild, argument);
        else
            return resolveMemberFromUsername(guild, argument);
    }

    private Member resolveMemberFromUsername(Guild guild, String argument) throws ArgumentParseException
    {
        List<Member> foundMembers = guild.getMembersByName(argument, true);
        if (foundMembers.size() < 1)
            throw new ArgumentParseException("Could not find member for given username = " + argument);
        else
            return foundMembers.get(0);
    }

    private Member resolveMemberFromMention(Guild guild, String mentionString) throws ArgumentParseException
    {
        String memberId = getMemberIdFromMention(mentionString);
        return Optional.ofNullable(guild.getMemberById(memberId))
                .orElseThrow(() -> new ArgumentParseException("Could not find member for given id = " + memberId));
    }
}
