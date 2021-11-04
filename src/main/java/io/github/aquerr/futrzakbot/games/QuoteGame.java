package io.github.aquerr.futrzakbot.games;

import io.github.aquerr.futrzakbot.FutrzakBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteGame
{
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final FutrzakBot futrzakBot;

    public QuoteGame(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
    }

    public void printQuote(TextChannel textChannel)
    {

        int max = 10;
        int min = 1;
        int i = RANDOM.nextInt(max - min + 1) + min;

        Message message = null;

        switch (i)
        {
            case 1:
                List<Member> members = textChannel.getGuild().getMembers();
                int userIndex = RANDOM.nextInt(members.size() + 1);
                message = new MessageBuilder().append(members.get(userIndex).getAsMention()).append(" to człowiek legenda! Mówię wam!").build();
                break;
            case 2:
                message = new MessageBuilder().append("Za IMPERATORA!").build();
                break;
            case 3:
                message = new MessageBuilder().append("Siemanko i uszanowako z tej strony Frik... W sumie lepiej nie wywoływać duchów.").build();
                break;
            case 4:
                message = new MessageBuilder().append("To ja, Futrzak!").build();
                break;
            case 5:
                message = new MessageBuilder().append("CO MNIE TYKASZ GŁUPCZE?!?!").build();
                break;
            case 6:
                message = new MessageBuilder().append("Jak terrorysta rąbie drzewo? Z zamachem. ( ͡° ͜ʖ ͡°)").build();
                break;
            case 7:
                message = new MessageBuilder().append("Chwalmy śłońce! \\[--]/").build();
                break;
            case 8:
                message = new MessageBuilder().append("Szybko! Zabij okno!").build();
                break;
            case 9:
                message = new MessageBuilder().append("Co tam słychać ").append("?").build();
                break;
            case 10:
                message = new MessageBuilder().append("Zjadłbym jakąś babeczkę!").build();
                break;
        }

        textChannel.sendMessage(message).queue();
    }
}
