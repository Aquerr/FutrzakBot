package io.github.aquerr.futrzakbot.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class FutrzakMessageEmbedFactory
{
    public static MessageEmbed create(String content)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription(content);
        return embedBuilder.build();
    }

    public static MessageEmbed createSongAddedToQueueMessage(String artist, String title)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setTitle("Song added to queue");
        embedBuilder.addField(new MessageEmbed.Field("Artist:", artist, false));
        embedBuilder.addField(new MessageEmbed.Field("Title:", title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createNowPlayingMessage(String artist, String title)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setTitle("Now playing");
        embedBuilder.addField(new MessageEmbed.Field("Artist:", artist, false));
        embedBuilder.addField(new MessageEmbed.Field("Title:", title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createSongNotFoundMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("Song not found. :(");
        return embedBuilder.build();
    }

    public static MessageEmbed createSongLoadFailedMessage(String localizedMessage)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle(localizedMessage);
        return embedBuilder.build();
    }
}
