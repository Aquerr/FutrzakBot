package io.github.aquerr.futrzakbot.discord.command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayer;
import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.exception.CommandException;
import io.github.aquerr.futrzakbot.discord.message.EmojiUnicodes;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerCommand implements Command, SlashCommand
{
    private static final String BUTTON_PLAY_PAUSE = "player_play_pause";
    private static final String BUTTON_NEXT_TRACK = "player_next_track";
    private static final String BUTTON_REPEAT = "player_repeat";
    private static final String BUTTON_SHOW_TRACK_QUEUE = "player_show_track_queue";
    private static final String BUTTON_REWIND_10_SECONDS = "player_rewind_10_seconds";
    private static final String BUTTON_REWIND_20_SECONDS = "player_rewind_20_seconds";
    private static final String BUTTON_SKIP_10_SECONDS = "player_skip_10_seconds";
    private static final String BUTTON_SKIP_20_SECONDS = "player_skip_20_seconds";
    private static final String BUTTON_FROM_BEGINNING = "player_from_beginning";

    private final FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private final FutrzakMessageEmbedFactory messageEmbedFactory;
    private final MessageSource messageSource;

    private final Map<String, Consumer<ButtonInteractionEvent>> BUTTON_HANDLES = Map.of(
            BUTTON_PLAY_PAUSE, this::buttonPlayPauseClick,
            BUTTON_NEXT_TRACK, this::buttonNextTrackClick,
            BUTTON_SHOW_TRACK_QUEUE, this::buttonShowQueue,
            BUTTON_REPEAT, this::buttonRepeatClick
    );

    public PlayerCommand(FutrzakAudioPlayerManager futrzakAudioPlayerManager,
                         FutrzakMessageEmbedFactory messageEmbedFactory,
                         MessageSource messageSource)
    {
        this.futrzakAudioPlayerManager = futrzakAudioPlayerManager;
        this.messageEmbedFactory = messageEmbedFactory;
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        GuildMessageChannel channel = context.getGuildMessageChannel();
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(channel.getGuild().getIdLong()).getPlayingTrack();
        if (audioTrack != null)
        {
            channel.sendMessageEmbeds(messageEmbedFactory.createNowPlayingMessage(audioTrack)).queue();
        }
        else
        {
            channel.sendMessageEmbeds(messageEmbedFactory.createNothingIsPlayingMessage()).queue();
        }
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("player");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.player.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.player.description");
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        return SlashCommand.super.getSlashCommandData();
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        GuildMessageChannel channel = event.getChannel().asGuildMessageChannel();
        AudioTrack audioTrack = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(channel.getGuild().getIdLong()).getPlayingTrack();
        ReplyCallbackAction replyCallbackAction = event.deferReply();
        if (audioTrack != null)
        {
            replyCallbackAction.addEmbeds(this.messageEmbedFactory.createNowPlayingMessage(audioTrack));
        }
        else
        {
            replyCallbackAction.addEmbeds(this.messageEmbedFactory.createNothingIsPlayingMessage());
        }

        replyCallbackAction
                .addActionRow(Button.secondary(BUTTON_SHOW_TRACK_QUEUE, "Queue"),
                        Button.secondary(BUTTON_PLAY_PAUSE, Emoji.fromUnicode(EmojiUnicodes.PLAY_PAUSE_BUTTON)),
                        Button.secondary(BUTTON_NEXT_TRACK, Emoji.fromUnicode(EmojiUnicodes.NEXT_TRACK)),
                        createRepeatButton(channel.getGuild().getIdLong()))
                .addActionRow(
                        Button.secondary(BUTTON_FROM_BEGINNING, Emoji.fromUnicode(EmojiUnicodes.FROM_BEGINNING)),
                        Button.secondary(BUTTON_REWIND_20_SECONDS, "Rewind 20 seconds").withEmoji(Emoji.fromUnicode(EmojiUnicodes.REWIND)),
                        Button.secondary(BUTTON_REWIND_10_SECONDS, "Rewind 10 seconds").withEmoji(Emoji.fromUnicode(EmojiUnicodes.REWIND)),
                        Button.secondary(BUTTON_SKIP_10_SECONDS, "Skip 10 seconds").withEmoji(Emoji.fromUnicode(EmojiUnicodes.FAST_FORWARD)),
                        Button.secondary(BUTTON_SKIP_20_SECONDS, "Skip 20 seconds").withEmoji(Emoji.fromUnicode(EmojiUnicodes.FAST_FORWARD)))
                .queue();
    }

    @Override
    public boolean supports(ButtonInteractionEvent event)
    {
        return true;
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) throws CommandException
    {
        BUTTON_HANDLES.get(event.getComponentId()).accept(event);
    }

    private Button createRepeatButton(long guildId)
    {
        FutrzakAudioPlayer futrzakAudioPlayer = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(guildId);
        if (futrzakAudioPlayer.isLoop())
        {
            return Button.success(BUTTON_REPEAT, Emoji.fromUnicode(EmojiUnicodes.REPEAT));
        }
        else
        {
            return Button.secondary(BUTTON_REPEAT, Emoji.fromUnicode(EmojiUnicodes.REPEAT));
        }
    }

    private void buttonPlayPauseClick(ButtonInteractionEvent event)
    {
        event.deferEdit().queue();
        long guildId = event.getGuild().getIdLong();
        FutrzakAudioPlayer futrzakAudioPlayer = this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(guildId);
        if (futrzakAudioPlayer.isPaused())
        {
            futrzakAudioPlayer.resume();
        }
        else
        {
            futrzakAudioPlayer.pause();
        }
    }

    private void buttonNextTrackClick(ButtonInteractionEvent event)
    {
        event.deferEdit().queue();
        this.futrzakAudioPlayerManager.skipAndPlayNextTrack(event.getGuild().getIdLong(), event.getGuildChannel());
    }

    private void buttonRepeatClick(ButtonInteractionEvent event)
    {
        long guildId = event.getGuild().getIdLong();
        this.futrzakAudioPlayerManager.getOrCreateAudioPlayer(guildId).toggleLoop();
        event.editButton(createRepeatButton(guildId)).queue();
    }

    private void buttonShowQueue(ButtonInteractionEvent event)
    {
        event.deferEdit().queue();
        event.getGuildChannel().sendMessageEmbeds(
                FutrzakMessageEmbedFactory.getInstance().createQueueMessage(
                        this.futrzakAudioPlayerManager.getQueue(event.getGuildChannel().getGuild().getIdLong()))).queue();
    }
}
