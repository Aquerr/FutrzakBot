package io.github.aquerr.futrzakbot.message;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class FutrzakMessageEmbedFactory
{
    private static final Color DEFAULT_COLOR = Color.GREEN;
    private static final Color ERROR_COLOR = Color.RED;

    public static MessageEmbed createSongAddedToQueueMessage(String artist, String title)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Utwór dodany do kolejki");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", artist, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł", title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createNowPlayingMessage(String artist, String title)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Obecnie gra");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", artist, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createSongNotFoundMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle("Utwór nie został znaleziony. :(");
        return embedBuilder.build();
    }

    public static MessageEmbed createSongLoadFailedMessage(String localizedMessage)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(localizedMessage);
        return embedBuilder.build();
    }

    public static MessageEmbed createQueueMessage(List<AudioTrack> queue)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);

        for (int i = 0; i < queue.size(); i++)
        {
            AudioTrack audioTrack = queue.get(i);
            embedBuilder.appendDescription((i + 1) + ". " + audioTrack.getInfo().author + " - " + audioTrack.getInfo().title + " " + ((int)audioTrack.getInfo().length / 1000 / 60) + ":" + audioTrack.getInfo().length / 1000 % 60 + "\n");
        }

        return embedBuilder.build();
    }

    public static MessageEmbed createPlayerStoppedMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Odtwarzacz został zatrzymany!");
        return embedBuilder.build();
    }

    public static MessageEmbed createPlayerResumedMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Odtwarzacz został wznowiony!");
        return embedBuilder.build();
    }

    public static MessageEmbed createPlayerVolumeChangedMessage(int volume)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Głośność została zmieniona na " + volume + "!");
        return embedBuilder.build();
    }
}
