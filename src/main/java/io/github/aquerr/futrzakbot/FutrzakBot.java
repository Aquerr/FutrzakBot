package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.config.Configuration;
import io.github.aquerr.futrzakbot.events.MessageListener;
import io.github.aquerr.futrzakbot.events.ReadyListener;
import io.github.aquerr.futrzakbot.games.FutrzakGame;
import io.github.aquerr.futrzakbot.games.GameManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FutrzakBot
{
    public static Path botDir = Paths.get(".").toAbsolutePath();

    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakBot.class);

    public static void main(String[] args)
    {
        FutrzakBot futrzakBot = new FutrzakBot();
        futrzakBot.start();
    }

    private JDA jda;

    private GameManager gameManager;

    private void start()
    {
        Configuration configuration = Configuration.loadConfiguration();

        try
        {
            this.gameManager = new GameManager(this);

            this.jda = JDABuilder.createDefault(configuration.getBotToken())
                    .addEventListeners(new MessageListener(this))
                    .addEventListeners(new ReadyListener())
                    .setAutoReconnect(true)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "FutrzakiShow !futrzak https://github.com/Aquerr/FutrzakBot"))
                .build();

            LOGGER.info("FutrzakBot Connected!");


            //Set up internal games...
            FutrzakGame.setup();
        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
    }

    public JDA getJda()
    {
        return jda;
    }

    public GameManager getGameManager()
    {
        return gameManager;
    }

    public static Path getBotDir()
    {
        return botDir;
    }
}
