package io.github.aquerr.futrzakbot.events;

import com.sun.net.ssl.internal.ssl.Provider;
import io.github.aquerr.futrzakbot.enums.MessagesEnum;
import io.github.aquerr.futrzakbot.games.EightBall;
import io.github.aquerr.futrzakbot.games.RouletteGame;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.EmoteImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

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

        if (event.getMessage().getContentDisplay().contains("kocham"))
        {
            event.getMessage().addReaction(":heart:").complete();
        }

        if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.COMMANDS.toString()))
        {
            //event.getChannel().sendMessage()

//            event.getChannel().sendMessage(new MessageEmbed(
//                    "",
//                    "List Komend",
//                    "- jeden - dwa \n - trzy",
//                    EmbedType.RICH,
//                    OffsetDateTime.now(),
//                    1,
//                    new MessageEmbed.Thumbnail(",","",50, 50),
//                    new MessageEmbed.Provider("", ""),
//                    new MessageEmbed.AuthorInfo("Nerdi", "url", "icon", ""),
//                    new MessageEmbed.VideoInfo("", 20, 20),
//                    new MessageEmbed.Footer("", "", ""),
//                    new MessageEmbed.ImageInfo("", "", 10, 10)));
        }
        else if (event.getMessage().getContentDisplay().startsWith(MessagesEnum.EIGHTBALL.toString()))
        {
            if (event.getMessage().getContentRaw().split(" ").length > 1)
            {
                EightBall.eightBall(event.getMessage(), event.getChannel(), event.getGuild().getId());
            }
            else
            {
                event.getChannel().sendMessage("Coś mi się wydaję że nie zadałeś żadnego pytania.").complete();
            }
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
                    message = channel.sendMessage("To ja, FutrzakBot!").complete();
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
    }
}
