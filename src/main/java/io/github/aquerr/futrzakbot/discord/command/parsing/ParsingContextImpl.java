package io.github.aquerr.futrzakbot.discord.command.parsing;

import lombok.Value;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Value
public class ParsingContextImpl implements ParsingContext
{
    MessageChannelUnion messageChannel;
    String argument;
}
