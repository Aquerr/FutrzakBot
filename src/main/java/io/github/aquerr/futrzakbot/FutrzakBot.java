package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.events.MessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;

import javax.security.auth.login.LoginException;

public class FutrzakBot
{
    public static void main(String[] args)
    {
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken("NDQ3MTEwNzI0OTI2MzA4MzUz.DeC-mg.fnpB19tWIQlz1iD8eAg8fFZ0upk")
                    .setGame(Game.of(Game.GameType.DEFAULT,"FutrzakiShow", "https://github.com/Aquerr/FutrzakBot"))
                    .buildBlocking();
            System.out.println("Connected!");
            jda.addEventListener(new MessageListener());
            jda.setAutoReconnect(true);
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
