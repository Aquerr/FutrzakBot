package io.github.aquerr.futrzakbot.events;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.hook.AudioOutputHookFactory;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.aquerr.futrzakbot.enums.MessagesEnum;
import io.github.aquerr.futrzakbot.games.EightBall;
import io.github.aquerr.futrzakbot.games.LoveMeter;
import io.github.aquerr.futrzakbot.games.RouletteGame;
import io.github.aquerr.futrzakbot.handlers.AudioPlayerSendHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.EmoteImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.requests.Route;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE))
        {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        }
        else
        {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }

        if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.COMMANDS.toString()))
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("Futrzak został stworzyony przez Nerdiego", "https://github.com/Aquerr/FutrzakBot");
            embedBuilder.setColor(Color.GREEN);
//            embedBuilder.setTitle("Lista komend");
            embedBuilder.setDescription("Oto spis komend, dostępnych u futrzaka: ");

            embedBuilder.addField(new MessageEmbed.Field(":boom: Ruletka: ", "!futrzak ruletka", false));
            embedBuilder.addField(new MessageEmbed.Field(":thought_balloon: Cytat: ", "!futrzak cytat", false));
            embedBuilder.addField(new MessageEmbed.Field(":question: 8Ball: ", "!futrzak 8ball", false));
            embedBuilder.addField(new MessageEmbed.Field(":microphone2: Dołącz na kanał głosowy: ", "!futrzak join", false));
            //embedBuilder.addBlankField(false);
            embedBuilder.addField(new MessageEmbed.Field(":heart: Licznik miłości: ", "!futrzak love", false));
            embedBuilder.addField(new MessageEmbed.Field(":tiger: Stworz swojego futrzaka: ", "!futrzak stworz", false));
            embedBuilder.addField(new MessageEmbed.Field(":crossed_swords: Walcz z innym futrzakiem: ", "!futrzak walka", false));

            event.getChannel().sendMessage(embedBuilder.build()).complete();
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.EIGHTBALL.toString()))
        {
            if (event.getMessage().getContentRaw().split("!futrzak 8ball").length > 1)
            {
                EightBall.eightBall(event.getMessage(), event.getChannel(), event.getGuild().getId());
            }
            else
            {
                event.getChannel().sendMessage("Coś mi się wydaję że nie zadałeś żadnego pytania.").complete();
            }
        }
        else if(event.getMessage().getContentDisplay().startsWith(MessagesEnum.LOVE.toString()))
        {
            Message loveMessage = LoveMeter.checkLove(event.getMessage());
            event.getChannel().sendMessage(loveMessage).queue();
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.ROULETTE.toString()))
        {
            if (!RouletteGame.isActive(event.getGuild().getId()))
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" rozpoczyna nową grę w ruletkę!").complete();
                event.getChannel().sendMessage("Będzie gorąco!").complete();
                RouletteGame.startNewGame(event.getGuild().getId());
            }

            boolean killed = RouletteGame.usePistol(event.getGuild().getId());

            if (killed)
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" pociąga za spust!").complete();
                event.getChannel().sendMessage("STRZAŁ!").complete();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" jest już w innym świecie :') ").complete();
            }
            else
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" pociąga za spust!").complete();
                event.getChannel().sendMessage("Z pistoletu słychać tylko odgłos kliknięcia!").complete();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" udało się przeżyć ruletkę.").complete();
            }
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.QUOTE.toString()))
        {
            MessageChannel channel = event.getChannel();
            Message message;

            Random random = new Random();
            int max = 10;
            int min = 1;
            int i = random.nextInt(max - min + 1) + min;

            switch (i)
            {
                case 1:
                    List<User> users = channel.getJDA().getUsers();
                    random = new Random();
                    int userIndex = random.nextInt(users.size() + 1);
                    message = channel.sendMessage(users.get(userIndex).getAsMention()).append(" to człowiek legenda! Mówię wam!").complete();
                    break;
                case 2:
                    message = channel.sendMessage("Za IMPERATORA!").complete();
                    break;
                case 3:
                    message = channel.sendMessage("Siemanko i uszanowako z tej strony Frik... W sumie lepiej nie wywoływać duchów.").complete();
                    break;
                case 4:
                    message = channel.sendMessage("To ja, Futrzak!").complete();
                    break;
                case 5:
                    message = channel.sendMessage("CO MNIE TYKASZ GŁUPCZE?!?!").complete();
                    break;
                case 6:
                    message = channel.sendMessage("Jak terrorysta rąbie drzewo? Z zamachem. ( ͡° ͜ʖ ͡°)").complete();
                    break;
                case 7:
                    message = channel.sendMessage("Chwalmy śłońce! \\[--]/").complete();
                    break;
                case 8:
                    message = channel.sendMessage("Szybko! Zabij okno!").complete();
                    break;
                case 9:
                    message = channel.sendMessage("Co tam słychać ").append(event.getMember().getAsMention()).append("?").complete();
                    break;
                case 10:
                    message = channel.sendMessage("Zjadłbym jakąś babeczkę!").complete();
                    break;
            }
        }
        else if(event.getMessage().getContentDisplay().startsWith("!futrzak debil"))
        {
            event.getChannel().sendMessage("To Ty ").append(event.getMember().getAsMention()).append(" :v").complete();
        }
        else if(event.getMessage().getContentDisplay().contains("Kocham") || event.getMessage().getContentDisplay().contains("kocham") || event.getMessage().getContentDisplay().contains("lofki")
                || event.getMessage().getContentDisplay().contains("loffciam") || event.getMessage().getContentDisplay().contains("stellar"))
        {
            event.getMessage().addReaction("❤").queue();
            //event.getMessage().addReaction(event.getJDA().getEmotesByName(":heart:", false).get(0)).queue();
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.CREATE.toString()))
        {
            event.getChannel().sendMessage("Ta funkcja jeszcze nie została w pełni dodana :/").complete();
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.JOIN.toString()))
        {
            event.getChannel().sendMessage("Ta funkcja jeszcze nie została w pełni dodana :/").complete();
//
//            VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
//            AudioManager audioManager = event.getGuild().getAudioManager();
//
//
//            AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
//
//            audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
//
//            audioPlayerManager.loadItemOrdered("1", "https://www.youtube.com/watch?v=CDbL51q0pNk", new AudioLoadResultHandler()
//            {
//                @Override
//                public void trackLoaded(AudioTrack track)
//                {
//
//                }
//
//                @Override
//                public void playlistLoaded(AudioPlaylist playlist)
//                {
//
//                }
//
//                @Override
//                public void noMatches()
//                {
//
//                }
//
//                @Override
//                public void loadFailed(FriendlyException exception)
//                {
//
//                }
//            });
//
//            audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayerManager.createPlayer()));
//
//            audioManager.openAudioConnection(voiceChannel);
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.FIGHT.toString()))
        {
            event.getChannel().sendMessage("Ta funkcja jeszcze nie została w pełni dodana :/").complete();
        }
    }
}
