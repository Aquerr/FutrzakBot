package io.github.aquerr.futrzakbot.discord.placeholder;

import lombok.Value;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

@Value
public class PlaceholderContext
{
    String text;
    TextChannel textChannel;
    Member member;
}
