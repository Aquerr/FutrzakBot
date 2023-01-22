package io.github.aquerr.futrzakbot.discord.command.parsing;

import lombok.Value;
import net.dv8tion.jda.api.entities.TextChannel;

@Value
public class ParsingContextImpl implements ParsingContext
{
    TextChannel textChannel;
    String argument;
}