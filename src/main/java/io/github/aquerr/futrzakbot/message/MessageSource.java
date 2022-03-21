package io.github.aquerr.futrzakbot.message;

import java.text.MessageFormat;

public class MessageSource
{
    private final Localization localization;

    public MessageSource(Localization localization)
    {
        this.localization = localization;
    }

    public String getMessage(String messageKey)
    {
        return localization.getMessage(messageKey);
    }

    public String getMessage(String messageKey, Object... args)
    {
        return MessageFormat.format(localization.getMessage(messageKey), args);
    }
}
