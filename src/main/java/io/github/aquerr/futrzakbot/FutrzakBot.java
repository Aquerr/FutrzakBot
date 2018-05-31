package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.events.MessageListener;
import io.github.aquerr.futrzakbot.secret.SecretProperties;
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
                    .setToken(SecretProperties.BOT_TOKEN)
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
