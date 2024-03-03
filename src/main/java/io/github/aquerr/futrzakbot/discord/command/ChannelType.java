package io.github.aquerr.futrzakbot.discord.command;

public enum ChannelType
{
    GUILD_TEXT(true),
    GUILD_PUBLIC_THREAD(true),
    PRIVATE_TEXT(false),
    UNKNOWN(false);

    private final boolean isGuild;

    ChannelType(boolean isGuild)
    {
        this.isGuild = isGuild;
    }

    public boolean isGuild()
    {
        return isGuild;
    }
}
