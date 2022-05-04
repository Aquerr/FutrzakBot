package io.github.aquerr.futrzakbot.message;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.aquerr.futrzakbot.PaginatedMessageEmbed;
import io.github.aquerr.futrzakbot.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class FutrzakMessageEmbedFactory
{
    public static final String HELP_MESSAGE_TITLE = "Futrzak - Lista Komend";

    private static final Color DEFAULT_COLOR = Color.GREEN;
    private static final Color ERROR_COLOR = Color.RED;

    public static MessageEmbed createSongAddedToQueueMessage(String artist, String title)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":white_check_mark: Utwór dodany do kolejki");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", artist, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createPlaylistAddedToQueueMessage(AudioPlaylist playlist)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":white_check_mark: Playlista " + playlist.getName() + " dodana do kolejki");
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
        int currentMinutes = (int)audioTrack.getPosition() / 60000;
        int currentSeconds = (int)audioTrack.getPosition() % 60000 / 1000;

        int totalMinutes = (int)audioTrack.getDuration() / 60000;
        int totalSeconds = (int)audioTrack.getDuration() % 60000 / 1000;

        int numberOfBars = 10;
        int bar = (int)(((double)audioTrack.getPosition() / audioTrack.getDuration()) * 100 / numberOfBars - 1);

        String[] progressBar = new String[numberOfBars];
        for (int i = 0; i < numberOfBars; i++)
        {
            if (bar >= i - 1)
            {
                progressBar[i] = ":blue_square:";
            }
            else
            {
                progressBar[i] = ":white_large_square:";
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":notes: Obecnie gra :notes:");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", audioTrack.getInfo().title, false));
        embedBuilder.addField(new MessageEmbed.Field("", String.format("%02d", currentMinutes) + ":" + String.format("%02d", currentSeconds)
                + " " + String.join("", progressBar) + " "
                + String.format("%02d", totalMinutes) + ":" + String.format("%02d", totalSeconds), false));
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
        embedBuilder.setTitle(":notes: Kolejka utworów :notes:");
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
        embedBuilder.setTitle(":pause_button: Odtwarzacz został zatrzymany!");
        return embedBuilder.build();
    }

    public static MessageEmbed createPlayerResumedMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":arrow_forward: Odtwarzacz został wznowiony!");
        return embedBuilder.build();
    }

    public static MessageEmbed createPlayerVolumeChangedMessage(int volume)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":loud_sound: Głośność została zmieniona na " + volume + "!");
        return embedBuilder.build();
    }

    public static MessageEmbed createSkipTrackMessage(AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":track_next: Pomijam utwór");
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createClearMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":recycle: Wyczyszczono kolejkę!");
        return embedBuilder.build();
    }

    public static MessageEmbed createRemoveMessage(int element, AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(":track_next: Pominołem utwór nr. " + element);
        embedBuilder.addField(new MessageEmbed.Field("Wykonawca:", audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field("Tytuł:", audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public static MessageEmbed createOutOfRangeMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(":warning: Niepoprawna pozycja utworu");
        return embedBuilder.build();
    }

    public static MessageEmbed createLoopMessage(boolean loop)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        if (loop)
        {
            embedBuilder.setTitle(":repeat: Zapętliłem kolejkę");
        }
        else
        {
            embedBuilder.setTitle(":repeat: Odpętliłem kolejkę");
        }
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

    public static MessageEmbed createHelpMessage(Collection<Command> commands, int page)
    {
        PaginatedMessageEmbed paginatedMessageEmbed = PaginatedMessageEmbed.ofFields(commands)
                .title(HELP_MESSAGE_TITLE, "https://github.com/Aquerr/FutrzakBot")
                .color(Color.GREEN)
                .fieldNamePopulator(Command::getName)
                .fieldValuePopulator(Command::getUsage)
                .linesPerPage(10)
                .build();

        return paginatedMessageEmbed.getPage(page);
    }
}
