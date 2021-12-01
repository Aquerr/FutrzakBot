package io.github.aquerr.futrzakbot.role;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.events.MessageListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleMessageReactListener extends ListenerAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final FutrzakBot futrzakBot;
    private final DiscordRoleGiver discordRoleGiver;

    public RoleMessageReactListener(final FutrzakBot futrzakBot, final DiscordRoleGiver discordRoleGiver)
    {
        this.futrzakBot = futrzakBot;
        this.discordRoleGiver = discordRoleGiver;
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event)
    {
        if (this.futrzakBot.getJda().getSelfUser().getIdLong() == event.getUserIdLong())
            return;

        LOGGER.debug("[" + event.getGuild().getName() + "] '"
                + event.getMember().getEffectiveName()
                + "' reacted on message "
                + event.getMessageId()
                + " in channel "
                + event.getChannel().getName());

        Member member = event.getMember();
        long messageId = event.getMessageIdLong();
        long textChannelId = event.getChannel().getIdLong();
        long guildId = event.getGuild().getIdLong();

        if (guildId == futrzakBot.getConfiguration().getGuildId()
            && textChannelId == futrzakBot.getConfiguration().getChannelId()
            && messageId == futrzakBot.getConfiguration().getMessageId())
        {
            LOGGER.info("[" + event.getGuild().getName() + "] Giving role to user '"
                    + event.getMember().getEffectiveName());
            discordRoleGiver.giveRole(member, event.getReaction());
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event)
    {
        if (this.futrzakBot.getJda().getSelfUser().getIdLong() == event.getUserIdLong())
            return;

        LOGGER.debug("[" + event.getGuild().getName() + "] '"
                + event.getMember().getEffectiveName()
                + "' reacted on message "
                + event.getMessageId()
                + " in channel "
                + event.getChannel().getName());

        Member member = event.getMember();
        long messageId = event.getMessageIdLong();
        long textChannelId = event.getChannel().getIdLong();
        long guildId = event.getGuild().getIdLong();

        if (guildId == futrzakBot.getConfiguration().getGuildId()
                && textChannelId == futrzakBot.getConfiguration().getChannelId()
                && messageId == futrzakBot.getConfiguration().getMessageId())
        {
            LOGGER.info("[" + event.getGuild().getName() + "] Removing role from user '"
                    + event.getMember().getEffectiveName());
            discordRoleGiver.removeRole(member, event.getReaction());
        }
    }
}
