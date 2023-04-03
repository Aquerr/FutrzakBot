package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.discord.command.QuoteCommand;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.command.parameters.StringParameter;
import io.github.aquerr.futrzakbot.discord.games.quote.QuoteCategory;
import io.github.aquerr.futrzakbot.discord.games.quote.QuoteGame;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuoteCommandTest
{
    private static final String CATEGORY_PARAM_KEY = "category";
    private static final String CATEGORY_NAME = "firstCategory";
    private static final String CATEGORY_ALIAS_1 = "categoryAlias1";
    private static final String CATEGORY_ALIAS_2 = "categoryAlias2";

    private static final String AVAILABLE_CATEGORIES = "command.quote.categories.available";
    private static final String QUOTE_COMMAND_CATEGORIES_HELP = "command.quote.categories.help";

    private static final String QUOTE_COMMAND_NAME_KEY = "command.quote.name";
    private static final String QUOTE_COMMAND_DESCRIPTION_KEY = "command.quote.description";
    private static final String QUOTE_COMMAND_NAME = ":thought_balloon: Cytat: ";
    private static final String COMMAND_QUOTE_DESCRIPTION = "Wylosuj cytat";

    @Mock
    private QuoteGame quoteGame;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private QuoteCommand quoteCommand;

    @Test
    void getAliasesShouldReturnCorrectAliases()
    {
        assertThat(quoteCommand.getAliases()).containsExactly("quote", "cytat");
    }

    @Test
    void getNameShouldReturnCorrectName()
    {
        given(messageSource.getMessage(QUOTE_COMMAND_NAME_KEY)).willReturn(QUOTE_COMMAND_NAME);

        assertThat(quoteCommand.getName()).isEqualTo(QUOTE_COMMAND_NAME);
    }

    @Test
    void getDescriptionShouldReturnCorrectDescription()
    {
        given(messageSource.getMessage(QUOTE_COMMAND_DESCRIPTION_KEY)).willReturn(COMMAND_QUOTE_DESCRIPTION);

        assertThat(quoteCommand.getDescription()).isEqualTo(COMMAND_QUOTE_DESCRIPTION);
    }

    @Test
    void getUsageShouldReturnCorrectUsage()
    {
        given(messageSource.getMessage(QUOTE_COMMAND_CATEGORIES_HELP)).willReturn("Use ? to show available categories.");

        assertThat(quoteCommand.getUsage()).isEqualTo("!f quote,cytat [<category>] " + "\n" + "Use ? to show available categories.");
    }

    @Test
    void getParametersReturnOneOptionalParameter()
    {
        assertThat(quoteCommand.getParameters()).containsExactly(StringParameter.builder().key(CATEGORY_PARAM_KEY).optional(true).build());
    }

    @Test
    void executeShouldPrintAvailableCategoriesWhenCategoryParameterIsQuestionMark() throws CommandException
    {
        // given
        CommandContext commandContext = mock(CommandContext.class);
        TextChannel textChannel = mock(TextChannel.class);
        given(textChannel.sendMessageEmbeds(any(MessageEmbed.class))).willReturn(mock(MessageCreateAction.class));
        given(commandContext.getTextChannel()).willReturn(textChannel);
        given(commandContext.get(CATEGORY_PARAM_KEY)).willReturn(Optional.of("?"));
        given(quoteGame.getAvailableCategories()).willReturn(prepareQuoteCategories());

        // when
        quoteCommand.execute(commandContext);

        // then
        verify(messageSource).getMessage(AVAILABLE_CATEGORIES);
        verify(textChannel).sendMessageEmbeds(any(MessageEmbed.class));
    }

    private List<QuoteCategory> prepareQuoteCategories()
    {
        QuoteCategory quoteCategory = new QuoteCategory();
        quoteCategory.setName(CATEGORY_NAME);
        quoteCategory.setAliases(Arrays.asList(CATEGORY_ALIAS_1, CATEGORY_ALIAS_2));
        return Arrays.asList(quoteCategory);
    }
}
