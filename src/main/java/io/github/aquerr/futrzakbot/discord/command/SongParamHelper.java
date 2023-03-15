package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.audio.AudioSource;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

public final class SongParamHelper
{
    private static final String PROTOCOL_REGEX = "^(http://)|(https://).*$";
    private static final String SONG_PARAM_KEY = "song";
    private static final String SOUNDCLOUD_SONG_PARAM_KEY = "soundcloud";
    private static final String YOUTUBE_SONG_PARAM_KEY = "youtube";

    public static String getSongNameFromSlashEvent(SlashCommandEvent event)
    {
        String songName = Optional.ofNullable(event.getOption(SONG_PARAM_KEY)).map(OptionMapping::getAsString).orElse(null);
        if (songName != null)
        {
            return getIdentifierForTrack(songName, AudioSource.UNKNOWN);
        }

        songName = Optional.ofNullable(event.getOption(SOUNDCLOUD_SONG_PARAM_KEY)).map(OptionMapping::getAsString).orElse(null);
        if (songName != null)
        {
            return getIdentifierForTrack(songName, AudioSource.SOUNDCLOUD);
        }

        songName = Optional.ofNullable(event.getOption(YOUTUBE_SONG_PARAM_KEY)).map(OptionMapping::getAsString).orElse(null);
        if (songName != null)
        {
            return getIdentifierForTrack(songName, AudioSource.YOUTUBE);
        }

        return songName;
    }

    public static String getIdentifierForTrack(String rawTrackName, AudioSource audioSource)
    {
        if (rawTrackName.matches(PROTOCOL_REGEX))
        {
            return rawTrackName;
        }
        else if (audioSource == AudioSource.UNKNOWN || audioSource == AudioSource.SOUNDCLOUD)
        {
            return "scsearch: " + rawTrackName;
        }
        return "ytsearch: " + rawTrackName;
    }

    private SongParamHelper()
    {

    }
}
