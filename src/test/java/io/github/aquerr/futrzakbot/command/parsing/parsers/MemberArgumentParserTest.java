package io.github.aquerr.futrzakbot.command.parsing.parsers;

import io.github.aquerr.futrzakbot.discord.command.exception.ArgumentParseException;
import io.github.aquerr.futrzakbot.discord.command.parsing.ParsingContext;
import io.github.aquerr.futrzakbot.discord.command.parsing.parsers.MemberArgumentParser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

class MemberArgumentParserTest
{
    private static final String MEMBER_AS_MENTION = "<@32130813908>";
    private static final String MEMBER_AS_NICKNAME = "Stefan";
    private static final String MEMBER_AS_NICKNAME_2 = "stefanooo";

    @InjectMocks
    private MemberArgumentParser memberArgumentParser;

    @BeforeEach
    void setUp()
    {
        openMocks(this);
    }

    @Test
    void parseShouldReturnMemberWhenContextContainsValidMemberMention()
    {
        // given
        ParsingContext parsingContext = prepareParsingContext(MEMBER_AS_MENTION);
        given(parsingContext.getTextChannel().getGuild().getMemberById(anyString())).willReturn(mock(Member.class));

        // when
        Member member = assertDoesNotThrow(() -> memberArgumentParser.parse(parsingContext));

        // then
        assertThat(member).isNotNull();
    }

    @Test
    void parseShouldReturnMemberWhenContextContainsValidMemberNickname()
    {
        // given
        ParsingContext parsingContext = prepareParsingContext(MEMBER_AS_NICKNAME);
        given(parsingContext.getTextChannel().getGuild().getMembersByName(anyString(), anyBoolean())).willReturn(Collections.singletonList(mock(Member.class)));

        // when
        Member member = assertDoesNotThrow(() -> memberArgumentParser.parse(parsingContext));

        // then
        assertThat(member).isNotNull();
    }

    @Test
    void parseShouldReturnFirstFoundMemberWithGivenNicknameWhenContextContainsValidMemberNickname()
    {
        // given
        ParsingContext parsingContext = prepareParsingContext(MEMBER_AS_NICKNAME);
        Member member1 = mock(Member.class);
        given(member1.getNickname()).willReturn(MEMBER_AS_NICKNAME_2);
        Member member2 = mock(Member.class);
        given(member2.getNickname()).willReturn(MEMBER_AS_NICKNAME);
        given(parsingContext.getTextChannel().getGuild().getMembersByName(anyString(), anyBoolean())).willReturn(Arrays.asList(member1, member2));

        // when
        Member member = assertDoesNotThrow(() -> memberArgumentParser.parse(parsingContext));

        // then
        assertThat(member).isNotNull();
        assertThat(member.getNickname()).isEqualTo(MEMBER_AS_NICKNAME_2);
    }

    @Test
    void parseShouldThrowArgumentParseExceptionWhenMemberCouldNotBeFoundForGivenNickname()
    {
        // given
        ParsingContext parsingContext = prepareParsingContext(MEMBER_AS_NICKNAME);

        // when
        // then
        assertThrows(ArgumentParseException.class, () -> memberArgumentParser.parse(parsingContext));
    }

    @Test
    void parseShouldThrowArgumentParseExceptionWhenMemberCouldNotBeFoundForGivenMention()
    {
        // given
        ParsingContext parsingContext = prepareParsingContext(MEMBER_AS_MENTION);

        // when
        // then
        assertThrows(ArgumentParseException.class, () -> memberArgumentParser.parse(parsingContext));
    }

    private ParsingContext prepareParsingContext(String argument)
    {
        ParsingContext parsingContext = mock(ParsingContext.class);
        given(parsingContext.getArgument()).willReturn(argument);

        TextChannel textChannel = mock(TextChannel.class);
        given(parsingContext.getTextChannel()).willReturn(textChannel);

        Guild guild = mock(Guild.class);
        given(textChannel.getGuild()).willReturn(guild);
        return parsingContext;
    }
}