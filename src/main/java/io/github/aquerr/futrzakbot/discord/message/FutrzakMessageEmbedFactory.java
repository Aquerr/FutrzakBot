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

import java.awt.Color;
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

    public MessageEmbed createNothingIsPlayingMessage()
    {
        return success(resolveMessage("embed.player.nothing-is-playing"));
    }

    public MessageEmbed createCreatingNewFutrzakMessage()
    {
        return success(resolveMessage("command.futrzak.no-furry-creating-new"));
    }

    public MessageEmbed createFunctionNotImplementedMessage()
    {
        return error(resolveMessage("error.function-not-implemented"));
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
        EmbedBuilder embedBuilder = prepareSuccess();
        embedBuilder.setTitle(resolveMessage("embed.player.song-added-to-queue"));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.artist"), artist, false));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.title"), title, false));
        return embedBuilder.build();
    }

    public MessageEmbed createPlaylistAddedToQueueMessage(AudioPlaylist playlist)
    {
        EmbedBuilder embedBuilder = prepareSuccess();
        embedBuilder.setTitle(resolveMessage("embed.player.playlist-added-to-queue", playlist.getName()));
        int count = Math.min(playlist.getTracks().size(), 10);
        for (int i = 0; i < count; i++)
        {
            AudioTrackInfo audioTrackInfo = playlist.getTracks().get(i).getInfo();
            embedBuilder.appendDescription((i + 1) + ". " + audioTrackInfo.author + " - " + audioTrackInfo.title + " " + ((int)audioTrackInfo.length / 1000 / 60) + ":" + audioTrackInfo.length / 1000 % 60 + "\n");
        }
        if (playlist.getTracks().size() > 10)
        {
            embedBuilder.appendDescription(resolveMessage("embed.player.more-tracks", playlist.getTracks().size() - count));
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

        EmbedBuilder embedBuilder = prepareSuccess();
        embedBuilder.setTitle(resolveMessage("embed.player.currently-player"));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.artist"), audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.title"), audioTrack.getInfo().title, false));
        embedBuilder.addField(new MessageEmbed.Field("", String.format("%02d", currentMinutes) + ":" + String.format("%02d", currentSeconds)
                + " " + String.join("", progressBar) + " "
                + String.format("%02d", totalMinutes) + ":" + String.format("%02d", totalSeconds), false));
        return embedBuilder.build();
    }

    public MessageEmbed createSongNotFoundMessage()
    {
        return error(resolveMessage("embed.player.track-could-not-be-found"));
    }

    public MessageEmbed createQueueMessage(List<AudioTrack> queue)
    {
        EmbedBuilder embedBuilder = prepareSuccess();
        embedBuilder.setTitle(resolveMessage("embed.player.track-queue"));

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
                            .replace("{length}", String.format("%02d", (int)audioTrackInfo.length / 1000 / 60) + ":" + String.format("%02d", audioTrackInfo.length / 1000 % 60))
                            .replace("{title}", audioTrackInfo.title)
                            .replace("{author}", audioTrackInfo.author)
                            .replace("{addedBy}", futrzakAdditionalAudioTrackData.getAddedBy().getAsMention());

            embedBuilder.appendDescription(queueLine);
        }

        if (queue.size() > maxTrackCount)
        {
            embedBuilder.appendDescription(resolveMessage("embed.player.more-tracks", queue.size() - count));
        }

        return embedBuilder.build();
    }

    public MessageEmbed createPlayerStoppedMessage()
    {
        return success(resolveMessage("embed.player.paused-player"));
    }

    public MessageEmbed createPlayerResumedMessage()
    {
        return success(resolveMessage("embed.player.resumed-player"));
    }

    public MessageEmbed createPlayerVolumeChangedMessage(int volume)
    {
        return success(resolveMessage("embed.player.volume-changed", volume));
    }

    public MessageEmbed createSkipTrackMessage(AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = prepareSuccess();
        embedBuilder.setTitle(resolveMessage("embed.player.skipping-track"));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.artist"), audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.title"), audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public MessageEmbed createClearMessage()
    {
        return success(resolveMessage("embed.player.cleared-queue"));
    }

    public MessageEmbed createRemoveMessage(int element, AudioTrack audioTrack)
    {
        EmbedBuilder embedBuilder = prepareSuccess();
        embedBuilder.setTitle(resolveMessage("embed.player.skipped-track-number", element));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.artist"), audioTrack.getInfo().author, false));
        embedBuilder.addField(new MessageEmbed.Field(resolveMessage("embed.player.title"), audioTrack.getInfo().title, false));
        return embedBuilder.build();
    }

    public MessageEmbed createWrongTrackPositionMessage()
    {
        return error(resolveMessage("embed.player.wrong-track-position"));
    }

    public MessageEmbed createLoopMessage(boolean loop)
    {
        String messageKey = loop ? "embed.player.activated-loop" : "embed.player.deactivated-loop";
        return success(resolveMessage(messageKey));
    }

    public MessageEmbed createSongErrorMessage(AudioTrack track)
    {
        EmbedBuilder embedBuilder = prepareError();
        embedBuilder.setTitle(resolveMessage("embed.player.error-has-occurred"));
        embedBuilder.addField(resolveMessage("embed.player.track"), track.getInfo().author + " " + track.getInfo().title, false);
        return embedBuilder.build();
    }

    public MessageEmbed createSongErrorMessage(AudioTrack track, FriendlyException exception)
    {
        EmbedBuilder embedBuilder = prepareError();
        embedBuilder.setTitle(resolveMessage("embed.player.error-has-occurred"));
        embedBuilder.addField(resolveMessage("embed.player.track"), track.getInfo().author + " " + track.getInfo().title, false);
        embedBuilder.setDescription(exception.getLocalizedMessage());
        return embedBuilder.build();
    }

    public MessageEmbed createHelpMessage(Collection<Command> commands, int page)
    {
        PaginatedMessageEmbed paginatedMessageEmbed = PaginatedMessageEmbed.ofFields(commands)
                .title(resolveMessage(COMMAND_LIST_MESSAGE_KEY), "https://github.com/Aquerr/FutrzakBot")
                .color(Color.GREEN)
                .fieldNamePopulator(Command::getName)
                .fieldValuePopulator(Command::getUsage)
                .linesPerPage(10)
                .build();

        return paginatedMessageEmbed.getPage(page);
    }

    public MessageEmbed error(String message)
    {
        return prepareError()
                .setTitle(message)
                .build();
    }

    public MessageEmbed success(String message)
    {
        return prepareSuccess()
                .setTitle(message)
                .build();
    }

    public EmbedBuilder prepareError()
    {
        return new EmbedBuilder().setColor(ERROR_COLOR);
    }

    public EmbedBuilder prepareSuccess()
    {
        return new EmbedBuilder().setColor(DEFAULT_COLOR);
    }

    private String resolveMessage(String messageKey, Object... args)
    {
        return messageSource.getMessage(messageKey, args);
    }
}
