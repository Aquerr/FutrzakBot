package io.github.aquerr.futrzakbot.discord.role;

import io.github.aquerr.futrzakbot.FutrzakBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DiscordRoleGiver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordRoleGiver.class);

    private final FutrzakBot futrzakBot;

    public DiscordRoleGiver(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
    }


    public void giveRole(Member member, MessageReaction reaction)
    {
        Guild guild = member.getGuild();
        Map<String, Long> emoteRoleIdsMap = this.futrzakBot.getConfiguration().getEmoteRoleIdsMap();
        Long roleId = emoteRoleIdsMap.get(reaction.getEmoji().getFormatted());
        if (roleId == null)
        {
            LOGGER.warn("Role id not configured for given emoji: {}", reaction.getEmoji().getFormatted());
            return;
        }

        Role role = guild.getRoleById(roleId);
        if (role == null)
        {
            LOGGER.warn("Role not found for given role id: {}", roleId);
            return;
        }
        guild.addRoleToMember(member, role).queue();
    }

    public void removeRole(Member member, MessageReaction reaction)
    {
        Guild guild = member.getGuild();
        Map<String, Long> emoteRoleIdsMap = this.futrzakBot.getConfiguration().getEmoteRoleIdsMap();
        Long roleId = emoteRoleIdsMap.get(reaction.getEmoji().getFormatted());
        if (roleId == null)
        {
            LOGGER.warn("Role id not configured for given emoji: {}", reaction.getEmoji().getFormatted());
            return;
        }

        Role role = guild.getRoleById(roleId);
        if (role == null)
        {
            LOGGER.warn("Role not found for given role id: {}", roleId);
            return;
        }

        guild.removeRoleFromMember(member, role).queue();
    }

    public void init()
    {
        prepareReactions();
    }

    private void prepareReactions()
    {
        Guild guild = this.futrzakBot.getJda().getGuildById(futrzakBot.getConfiguration().getGuildId());
        MessageChannel channel = guild.getTextChannelById(futrzakBot.getConfiguration().getChannelId());
        long messageId = this.futrzakBot.getConfiguration().getMessageId();
        LOGGER.info("Adding role reactions to message in guild {}, channel {}, message {}", guild.getId(), channel.getId(), messageId);

        Map<String, Long> emoteRoleIdsMap = this.futrzakBot.getConfiguration().getEmoteRoleIdsMap();
        for (final Map.Entry<String, Long> emoteRoleEntry : emoteRoleIdsMap.entrySet())
        {
            Emoji emoji = Emoji.fromUnicode(emoteRoleEntry.getKey());
            channel.addReactionById(messageId, emoji).queue();
        }
    }
}
