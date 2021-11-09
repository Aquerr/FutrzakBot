package io.github.aquerr.futrzakbot.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface Command
{
    boolean execute(Member member, TextChannel textChannel, List<String> args);

    String getUsage();

    String getHelpName();
}
