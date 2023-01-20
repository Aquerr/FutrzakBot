package io.github.aquerr.futrzakbot.discord.placeholder;

public enum Placeholders
{
    RANDOM_MEMBER("random_member");

    String placeholder;

    Placeholders(String placeholder)
    {
        this.placeholder = placeholder;
    }

    public String getPlaceholder()
    {
        return placeholder;
    }
}
