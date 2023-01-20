package io.github.aquerr.futrzakbot.message;

import io.github.aquerr.futrzakbot.discord.message.Localization;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageSourceTest
{
    private static final String MESSAGE_KEY = "messageKey";
    private static final String MESSAGE_VALUE = "messageValue";
    private static final String MESSAGE_VALUE_WITH_FORMAT_PLACEHOLDER = "formatted message: {0}";
    private static final String MESSAGE_ARG = "arg";
    private static final String EXPECTED_FORMATTED_MESSAGE = "formatted message: arg";

    @Mock
    private Localization localization;

    @InjectMocks
    private MessageSource messageSource;

    @Test
    void getMessageShouldPassMessageKeyToLocalizationAndReturnLocalizedStringValue()
    {
        // given
        given(localization.getMessage(MESSAGE_KEY)).willReturn(MESSAGE_VALUE);

        // when
        String result = messageSource.getMessage(MESSAGE_KEY);

        // then
        assertThat(result).isEqualTo(MESSAGE_VALUE);
    }

    @Test
    void getMessageShouldPassMessageKeyToLocalizationAndFormatItsArgs()
    {
        // given
        given(localization.getMessage(MESSAGE_KEY)).willReturn(MESSAGE_VALUE_WITH_FORMAT_PLACEHOLDER);

        // when
        String result = messageSource.getMessage(MESSAGE_KEY, MESSAGE_ARG);

        // then
        assertThat(result).isEqualTo(EXPECTED_FORMATTED_MESSAGE);
    }
}