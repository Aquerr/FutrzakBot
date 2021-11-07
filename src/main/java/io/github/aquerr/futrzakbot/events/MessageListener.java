package io.github.aquerr.futrzakbot.events;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.audio.handler.AudioPlayerSendHandler;
import io.github.aquerr.futrzakbot.enums.CommandsEnum;
import io.github.aquerr.futrzakbot.games.EightBall;
import io.github.aquerr.futrzakbot.games.LoveMeter;
import io.github.aquerr.futrzakbot.games.RouletteGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.io.IOException;

public class MessageListener extends ListenerAdapter
{
    private final FutrzakBot futrzakBot;

    public MessageListener(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        TextChannel textChannel = event.getTextChannel();
        long guildId = event.getGuild().getIdLong();
        Member member = event.getMember();

        if (event.isFromType(ChannelType.PRIVATE))
        {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        }
        else
        {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), member.getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }

        if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.COMMANDS.toString()))
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("Futrzak został stworzyony przez Nerdiego", "https://github.com/Aquerr/FutrzakBot");
            embedBuilder.setColor(Color.GREEN);
//            embedBuilder.setTitle("Lista komend");
            embedBuilder.setDescription("Oto spis komend, dostępnych u futrzaka: ");

            embedBuilder.addField(new MessageEmbed.Field(":boom: Ruletka: ", "!futrzak ruletka", false));
            embedBuilder.addField(new MessageEmbed.Field(":thought_balloon: Cytat: ", "!futrzak cytat", false));
            embedBuilder.addField(new MessageEmbed.Field(":question: 8Ball: ", "!futrzak 8ball <pytanie>", false));
            embedBuilder.addField(new MessageEmbed.Field(":microphone2: Odtwórz podany utwór: ", "!futrzak play <nazwa utworu>", false));
            //embedBuilder.addBlankField(false);
            embedBuilder.addField(new MessageEmbed.Field(":heart: Licznik miłości: ", "!futrzak love <użytkownik>", false));
            embedBuilder.addField(new MessageEmbed.Field(":tiger: Stworz swojego futrzaka: ", "!futrzak stworz", false));
            embedBuilder.addField(new MessageEmbed.Field(":crossed_swords: Walcz z innym futrzakiem: ", "!futrzak walka", false));
            embedBuilder.addField(new MessageEmbed.Field(":tiger: Sprawdź stan swojego futrzaka: ", "!futrzak futrzak", false));

            event.getChannel().sendMessage(embedBuilder.build()).complete();
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.EIGHTBALL.toString()))
        {
            if (event.getMessage().getContentRaw().split("!futrzak 8ball").length > 1)
            {
                EightBall.eightBall(event.getMessage(), event.getChannel(), guildId);
            }
            else
            {
                event.getChannel().sendMessage("Coś mi się wydaję że nie zadałeś żadnego pytania.").complete();
            }
        }
        else if(event.getMessage().getContentDisplay().startsWith(CommandsEnum.LOVE.toString()))
        {
            Message loveMessage = LoveMeter.checkLove(event.getMessage());
            event.getChannel().sendMessage(loveMessage).queue();
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.ROULETTE.toString()))
        {
            if (!RouletteGame.isActive(guildId))
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" rozpoczyna nową grę w ruletkę!").complete();
                event.getChannel().sendMessage("Będzie gorąco!").complete();
                RouletteGame.startNewGame(guildId);
            }

            boolean killed = RouletteGame.usePistol(guildId);

            if (killed)
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" pociąga za spust!").complete();
                event.getChannel().sendMessage("STRZAŁ!").complete();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" jest już w innym świecie :') ").complete();
                event.getGuild().mute(member, true).reason("Mutuję Cię na 30sekund!").complete();
            }
            else
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" pociąga za spust!").complete();
                event.getChannel().sendMessage("Z pistoletu słychać tylko odgłos kliknięcia!").complete();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).append(" udało się przeżyć ruletkę.").complete();
            }
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.QUOTE.toString()))
        {
            this.futrzakBot.getGameManager().getQuoteGame().printQuote(textChannel);
        }
        else if(event.getMessage().getContentDisplay().startsWith("!futrzak debil"))
        {
            event.getChannel().sendMessage("To Ty ").append(member.getAsMention()).append(" :v").complete();
        }
        else if(event.getMessage().getContentDisplay().contains("Kocham") || event.getMessage().getContentDisplay().contains("kocham") || event.getMessage().getContentDisplay().contains("lofki")
                || event.getMessage().getContentDisplay().contains("loffciam") || event.getMessage().getContentDisplay().contains("stellar"))
        {
            event.getMessage().addReaction("❤").queue();
            //event.getMessage().addReaction(event.getJDA().getEmotesByName(":heart:", false).get(0)).queue();
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.CREATE.toString()))
        {
            event.getChannel().sendMessage("Ta funkcja jeszcze nie została w pełni dodana :/ (ale Twój futrzak został już utworzony)").complete();

            try
            {
                this.futrzakBot.getGameManager().getFutrzakGame().createFutrzak(guildId, event.getAuthor().getId());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.DISPLAY.toString()))
        {
            if(this.futrzakBot.getGameManager().getFutrzakGame().checkIfFutrzakExists(guildId, event.getAuthor().getId()))
                event.getChannel().sendMessage(this.futrzakBot.getGameManager().getFutrzakGame().displayFutrzak(event.getGuild().getId(), event.getAuthor())).queue();
            else event.getChannel().sendMessage("Widzę że nie masz jeszcze swojego futrzaka. Możesz go stowrzyć za pomocą komendy \"!futrzak stworz\"").queue();
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.PLAY.toString()))
        {
            GuildVoiceState guildVoiceState = member.getVoiceState();
            VoiceChannel voiceChannel = guildVoiceState.getChannel();
            if (voiceChannel == null)
            {
                textChannel.sendMessage("Aby użyć tej komendy musisz być na kanale głosowym!").complete();
            }
            else
            {
                this.futrzakBot.getJda().getSelfUser();
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.setSendingHandler(new AudioPlayerSendHandler(FutrzakAudioPlayerManager.getInstance().getOrCreateAudioPlayer(guildId).getInternalAudioPlayer()));
                audioManager.openAudioConnection(voiceChannel);
                String songName = event.getMessage().getContentDisplay().substring(CommandsEnum.PLAY.toString().length());
                FutrzakAudioPlayerManager.getInstance().queue(guildId, textChannel, songName);
            }
        }
        else if(event.getMessage().getContentDisplay().startsWith(CommandsEnum.NEXT.toString()))
        {
            FutrzakAudioPlayerManager.getInstance().playNextTrack(guildId, textChannel);
        }
        else if(event.getMessage().getContentDisplay().startsWith(CommandsEnum.STOP.toString()))
        {
            FutrzakAudioPlayerManager.getInstance().stop(guildId, textChannel);
        }
        else if (event.getMessage().getContentDisplay().startsWith(CommandsEnum.FIGHT.toString()))
        {
            event.getChannel().sendMessage("Ta funkcja jeszcze nie została w pełni dodana :/").complete();
        }
    }
}
