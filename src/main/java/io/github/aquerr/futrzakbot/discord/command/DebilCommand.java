package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collections;
import java.util.List;

public class DebilCommand implements Command, SlashCommand
{
    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel channel = context.getTextChannel();
        Member member = context.getMember();
        channel.sendMessage("To Ty " + member.getAsMention() + " :clown:").complete();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("debil");
    }

    @Override
    public String getUsage()
    {
        return CommandManager.COMMAND_PREFIX + " debil <user>";
    }

    @Override
    public String getName()
    {
        return ":japanese_goblin: debil?";
    }

    @Override
    public String getDescription()
    {
        return "Debil?";
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        event.reply("To Ty " + event.getMember().getAsMention() + " :clown:").queue();
    }
}
