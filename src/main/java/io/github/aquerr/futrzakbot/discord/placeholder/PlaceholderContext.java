package io.github.aquerr.futrzakbot.discord.placeholder;

import lombok.Value;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Value
public class PlaceholderContext
{
    String text;
    MessageChannel messageChannel;
    Member member;
}
