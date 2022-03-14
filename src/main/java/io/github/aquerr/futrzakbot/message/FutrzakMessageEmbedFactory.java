package io.github.aquerr.futrzakbot.message;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createPlaylistAddedToQueueMessage(AudioPlaylist playlist)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Playlista " + playlist.getName() + " dodana do kolejki");
        int count = Math.min(playlist.getTracks().size(), 10);
        for (int i = 0; i < count; i++)
        {
            AudioTrackInfo audioTrackInfo = playlist.getTracks().get(i).getInfo();
            embedBuilder.appendDescription((i + 1) + ". " + audioTrackInfo.author + " - " + audioTrackInfo.title + " " + ((int)audioTrackInfo.length / 1000 / 60) + ":" + audioTrackInfo.length / 1000 % 60 + "\n");
        }
        if (playlist.getTracks().size() > 10)
        {
            embedBuilder.appendDescription("+ " + (playlist.getTracks().size() - count) + " innych");
        }
        return embedBuilder.build();
    }

    public static MessageEmbed createNowPlayingMessage(AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Obecnie gra");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", audioTrack.getInfo().title, false));
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
        embedBuilder.setTitle("Kolejka utworów");
        embedBuilder.setColor(DEFAULT_COLOR);

        int maxTrackCount = 20;
        int count = Math.min(queue.size(), maxTrackCount);

        for (int i = 0; i < count; i++)
        {
            AudioTrackInfo audioTrackInfo = queue.get(i).getInfo();
            embedBuilder.appendDescription((i + 1) + ". " + audioTrackInfo.author + " - " + audioTrackInfo.title + " " + ((int)audioTrackInfo.length / 1000 / 60) + ":" + audioTrackInfo.length / 1000 % 60 + "\n");
        }

        if (queue.size() > maxTrackCount)
        {
            embedBuilder.appendDescription("+ " + (queue.size() - count) + " innych");
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

    public static MessageEmbed createSkipTrackMessage(AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle("Pomijam utwór");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createSongErrorMessage(AudioTrack track)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle("Wystąpił błąd!");
        embedBuilder.addField("Utwór: ", track.getInfo().author + " " + track.getInfo().title, false);
        return embedBuilder.build();
    }

    public static MessageEmbed createSongErrorMessage(AudioTrack track, FriendlyException exception)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle("Wystąpił błąd!");
        embedBuilder.setDescription(exception.getLocalizedMessage());
        return embedBuilder.build();
    }
}
