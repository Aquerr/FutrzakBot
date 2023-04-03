package io.github.aquerr.futrzakbot.discord.role;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import io.github.aquerr.futrzakbot.FutrzakBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Map;

public class DiscordRoleGiver
{
    private final FutrzakBot futrzakBot;

    public DiscordRoleGiver(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
    }


    public void giveRole(Member member, MessageReaction reaction)
    {
        Guild guild = member.getGuild();
        Emoji emoji = EmojiManager.getByUnicode(reaction.getEmoji().getFormatted());
        Map<String, Long> emoteRoleIdsMap = this.futrzakBot.getConfiguration().getEmoteRoleIdsMap();
        Long roleId = emoteRoleIdsMap.get(EmojiParser.parseToAliases(emoji.getUnicode()));
        guild.addRoleToMember(member, guild.getRoleById(roleId)).queue();
    }

    public void removeRole(Member member, MessageReaction reaction)
    {
        Guild guild = member.getGuild();
        Emoji emoji = EmojiManager.getByUnicode(reaction.getEmoji().getFormatted());
        Map<String, Long> emoteRoleIdsMap = this.futrzakBot.getConfiguration().getEmoteRoleIdsMap();
        Long roleId = emoteRoleIdsMap.get(EmojiParser.parseToAliases(emoji.getUnicode()));
        guild.removeRoleFromMember(member, guild.getRoleById(roleId)).queue();
    }

    public void init()
    {
        prepareReactions();
    }

    private void prepareReactions()
    {
        Guild guild = this.futrzakBot.getJda().getGuildById(futrzakBot.getConfiguration().getGuildId());
        TextChannel textChannel = guild.getTextChannelById(futrzakBot.getConfiguration().getChannelId());
        long messageId = this.futrzakBot.getConfiguration().getMessageId();
        Map<String, Long> emoteRoleIdsMap = this.futrzakBot.getConfiguration().getEmoteRoleIdsMap();
        for (final Map.Entry<String, Long> emoteRoleEntry : emoteRoleIdsMap.entrySet())
        {
            Emoji emoji = EmojiManager.getForAlias(emoteRoleEntry.getKey());
            textChannel.addReactionById(messageId, net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(emoji.getUnicode())).queue();
        }
    }
}
