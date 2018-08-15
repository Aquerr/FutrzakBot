package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.events.MessageListener;
import io.github.aquerr.futrzakbot.games.FutrzakGame;
import io.github.aquerr.futrzakbot.secret.SecretProperties;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FutrzakBot
{
    public static Path botDir = Paths.get(new File("").getAbsolutePath());

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


            //Set up internal games...
            FutrzakGame.setup();
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
