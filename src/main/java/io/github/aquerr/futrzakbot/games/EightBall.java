package io.github.aquerr.futrzakbot.games;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.Random;

public class EightBall
{


    public static void eightBall(Message question, MessageChannel channel)
    {
        Message message;

        Random random = new Random();
        int max = 10;
        int min = 1;
        int i = random.nextInt(max - min + 1) + min;

        switch (i)
        {
            case 1:
                message = channel.sendMessage("Sądzę że to możliwe.").complete();
                break;
            case 2:
                message = channel.sendMessage("Zdecydowanie TAK!").complete();
                break;
            case 3:
                List<User> users = channel.getJDA().getUsers();
                random = new Random();
                int userIndex = random.nextInt(users.size() + 1);
                message = channel.sendMessage("Zapytaj ").append(users.get(userIndex).getAsMention()).append("!").append(" Ta osoba zna odpowiedź.").complete();
                break;
            case 4:
                message = channel.sendMessage("Raczej nie..").complete();
                break;
            case 5:
                message = channel.sendMessage("Wątpię w to.").complete();
                break;
            case 6:
                message = channel.sendMessage("Odpowiedź na to pytanie jest zapisana w gwiazdach.").complete();
                break;
            case 7:
                message = channel.sendMessage("Wydaje mi się że tak.").complete();
                break;
            case 8:
                message = channel.sendMessage("Przemilczę to pytanie...").complete();
                break;
            case 9:
                message = channel.sendMessage("Sory, ale to pytanie jest za trudne na mój mózg.").complete();
                break;
            case 10:
                message = channel.sendMessage("Nie licz na to.").complete();
                break;
        }
    }
}
