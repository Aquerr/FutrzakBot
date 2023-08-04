package io.github.aquerr.futrzakbot.discord.message;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAdditionalAudioTrackData;
import io.github.aquerr.futrzakbot.discord.util.PaginatedMessageEmbed;
import io.github.aquerr.futrzakbot.discord.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class FutrzakMessageEmbedFactory
{
    private static final String SONG_PROGRESS_PLAYED_ICON_CODE = ":blue_square:";
    private static final String SONG_PROGRESS_REMAINING_ICON_CODE = ":white_large_square:";
    private static final String COMMAND_LIST_MESSAGE_KEY = "embed.command-list";

    public static FutrzakMessageEmbedFactory getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public static void init(MessageSource messageSource)
    {
        InstanceHolder.INSTANCE = new FutrzakMessageEmbedFactory(messageSource);
    }

    private static class InstanceHolder
    {

        public static FutrzakMessageEmbedFactory INSTANCE = null;
    }

    public static final Color DEFAULT_COLOR = Color.GREEN;
    public static final Color ERROR_COLOR = Color.RED;

    private final MessageSource messageSource;

    private FutrzakMessageEmbedFactory(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    public MessageEmbed createSongAddedToQueueMessage(String artist, String title)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(getMessage("embed.player.song-added-to-queue"));
        embedBuilder.addField(new MessageEmbed.Field(getMessage("embed.player.artist"), artist, false));
        embedBuilder.addField(new MessageEmbed.Field(getMessage("embed.player.title"), title, false));
        return embedBuilder.build();
    }

    public MessageEmbed createPlaylistAddedToQueueMessage(AudioPlaylist playlist)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.playlist-added-to-queue", playlist.getName()));
        int count = Math.min(playlist.getTracks().size(), 10);
        for (int i = 0; i < count; i++)
        {
            AudioTrackInfo audioTrackInfo = playlist.getTracks().get(i).getInfo();
            embedBuilder.appendDescription((i + 1) + ". " + audioTrackInfo.author + " - " + audioTrackInfo.title + " " + ((int)audioTrackInfo.length / 1000 / 60) + ":" + audioTrackInfo.length / 1000 % 60 + "\n");
        }
        if (playlist.getTracks().size() > 10)
        {
            embedBuilder.appendDescription(messageSource.getMessage("embed.player.more-tracks", playlist.getTracks().size() - count));
        }
        return embedBuilder.build();
    }

    public MessageEmbed createNowPlayingMessage(AudioTrack audioTrack)
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
                progressBar[i] = SONG_PROGRESS_PLAYED_ICON_CODE;
            }
            else
            {
                progressBar[i] = SONG_PROGRESS_REMAINING_ICON_CODE;
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.currently-player"));
        embedBuilder.addField(new MessageEmbed.Field(messageSource.getMessage("embed.player.artist"), audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field(messageSource.getMessage("embed.player.title"), audioTrack.getInfo().title, false));
        embedBuilder.addField(new MessageEmbed.Field("", String.format("%02d", currentMinutes) + ":" + String.format("%02d", currentSeconds)
                + " " + String.join("", progressBar) + " "
                + String.format("%02d", totalMinutes) + ":" + String.format("%02d", totalSeconds), false));
        return embedBuilder.build();
    }

    public MessageEmbed createSongNotFoundMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.track-could-not-be-found"));
        return embedBuilder.build();
    }

    public MessageEmbed createSongLoadFailedMessage(String localizedMessage)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(localizedMessage);
        return embedBuilder.build();
    }

    public MessageEmbed createQueueMessage(List<AudioTrack> queue)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(messageSource.getMessage("embed.player.track-queue"));
        embedBuilder.setColor(DEFAULT_COLOR);

        int maxTrackCount = 20;
        int count = Math.min(queue.size(), maxTrackCount);

        for (int i = 0; i < count; i++)
        {
            AudioTrack audioTrack = queue.get(i);
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            FutrzakAdditionalAudioTrackData futrzakAdditionalAudioTrackData = audioTrack.getUserData(FutrzakAdditionalAudioTrackData.class);
            String queueLine = "{position}. `{length}` {title} - {author} {addedBy}\n";
            queueLine = queueLine
                            .replace("{position}", String.valueOf(i + 1))
                            .replace("{length}", (int)audioTrackInfo.length / 1000 / 60 + ":" + audioTrackInfo.length / 1000 % 60)
                            .replace("{title}", audioTrackInfo.title)
                            .replace("{author}", audioTrackInfo.author)
                            .replace("{addedBy}", futrzakAdditionalAudioTrackData.getAddedBy().getAsMention());

            embedBuilder.appendDescription(queueLine);
        }

        if (queue.size() > maxTrackCount)
        {
            embedBuilder.appendDescription(messageSource.getMessage("embed.player.more-tracks", queue.size() - count));
        }

        return embedBuilder.build();
    }

    public MessageEmbed createPlayerStoppedMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.paused-player"));
        return embedBuilder.build();
    }

    public MessageEmbed createPlayerResumedMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.resumed-player"));
        return embedBuilder.build();
    }

    public MessageEmbed createPlayerVolumeChangedMessage(int volume)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.volume-changed", volume));
        return embedBuilder.build();
    }

    public MessageEmbed createSkipTrackMessage(AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.skipping-track"));
        embedBuilder.addField(new MessageEmbed.Field(messageSource.getMessage("embed.player.artist"), audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field(messageSource.getMessage("embed.player.title"), audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public MessageEmbed createClearMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.cleared-queue"));
        return embedBuilder.build();
    }

    public MessageEmbed createRemoveMessage(int element, AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.skipped-track-number", element));
        embedBuilder.addField(new MessageEmbed.Field(messageSource.getMessage("embed.player.artist"), audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field(messageSource.getMessage("embed.player.title"), audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public MessageEmbed createOutOfRangeMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.wrong-track-position"));
        return embedBuilder.build();
    }

    public MessageEmbed createLoopMessage(boolean loop)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(DEFAULT_COLOR);
        if (loop)
        {
            embedBuilder.setTitle(messageSource.getMessage("embed.player.activated-loop"));
        }
        else
        {
            embedBuilder.setTitle(messageSource.getMessage("embed.player.deactivated-loop"));
        }
        return embedBuilder.build();
    }

    public MessageEmbed createSongErrorMessage(AudioTrack track)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.error-has-occurred"));
        embedBuilder.addField(messageSource.getMessage("embed.player.track"), track.getInfo().author + " " + track.getInfo().title, false);
        return embedBuilder.build();
    }

    public MessageEmbed createSongErrorMessage(AudioTrack track, FriendlyException exception)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ERROR_COLOR);
        embedBuilder.setTitle(messageSource.getMessage("embed.player.error-has-occurred"));
        embedBuilder.addField(messageSource.getMessage("embed.player.track"), track.getInfo().author + " " + track.getInfo().title, false);
        embedBuilder.setDescription(exception.getLocalizedMessage());
        return embedBuilder.build();
    }

    public MessageEmbed createHelpMessage(Collection<Command> commands, int page)
    {
        PaginatedMessageEmbed paginatedMessageEmbed = PaginatedMessageEmbed.ofFields(commands)
                .title(getMessage(COMMAND_LIST_MESSAGE_KEY), "https://github.com/Aquerr/FutrzakBot")
                .color(Color.GREEN)
                .fieldNamePopulator(Command::getName)
                .fieldValuePopulator(Command::getUsage)
                .linesPerPage(10)
                .build();

        return paginatedMessageEmbed.getPage(page);
    }

    private String getMessage(String messageKey)
    {
        return messageSource.getMessage(messageKey);
    }
}
