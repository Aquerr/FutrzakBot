package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.games.FutrzakGame;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FutrzakCommand implements Command, SlashCommand
{
    private final FutrzakGame futrzakGame;
    private final MessageSource messageSource;

    public FutrzakCommand(FutrzakGame futrzakGame, MessageSource messageSource)
    {
        this.futrzakGame = futrzakGame;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context) throws CommandException
    {
        GuildMessageChannel channel = context.getGuildMessageChannel();
        Member member = context.getMember();
        long guildId = channel.getGuild().getIdLong();
        channel.sendMessageEmbeds(showFutrzak(channel, guildId, member)).queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("futrzak");
    }

    @Override
    public String getName()
    {
        return this.messageSource.getMessage("command.futrzak.name");
    }

    @Override
    public String getDescription()
    {
        return this.messageSource.getMessage("command.futrzak.description");
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) throws CommandException
    {
        ReplyCallbackAction replyCallbackAction = event.deferReply();
        MessageEmbed messageEmbed = showFutrzak(event.getChannel().asGuildMessageChannel(), event.getGuild().getIdLong(), event.getMember());
        replyCallbackAction.addEmbeds(messageEmbed).complete();
    }

    private MessageEmbed showFutrzak(GuildMessageChannel channel, long guildId, Member member) throws CommandException
    {
        if(this.futrzakGame.checkIfFutrzakExists(guildId, member.getId()))
        {
            return this.futrzakGame.displayFutrzak(guildId, member);
        }
        else
        {
            MessageEmbed messageEmbed = FutrzakMessageEmbedFactory.getInstance().createCreatingNewFutrzakMessage();
            try
            {
                this.futrzakGame.createFutrzak(channel.getGuild().getIdLong(), member.getId());
                return messageEmbed;
            }
            catch (IOException e)
            {
                throw new CommandException(e);
            }
        }
    }
}
