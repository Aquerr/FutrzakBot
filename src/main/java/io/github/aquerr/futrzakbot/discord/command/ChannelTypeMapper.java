package io.github.aquerr.futrzakbot.discord.command;

import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.util.Map;

public class ChannelTypeMapper
{
    private static final Map<ChannelType, io.github.aquerr.futrzakbot.discord.command.ChannelType> MAPPING = Map.of(
            ChannelType.TEXT, io.github.aquerr.futrzakbot.discord.command.ChannelType.GUILD_TEXT,
            ChannelType.PRIVATE, io.github.aquerr.futrzakbot.discord.command.ChannelType.PRIVATE_TEXT,
            ChannelType.GUILD_PUBLIC_THREAD, io.github.aquerr.futrzakbot.discord.command.ChannelType.GUILD_PUBLIC_THREAD
    );

    public static io.github.aquerr.futrzakbot.discord.command.ChannelType map(ChannelType channelType)
    {
        return MAPPING.getOrDefault(channelType, io.github.aquerr.futrzakbot.discord.command.ChannelType.UNKNOWN);
    }
}
