package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.CommandManager;
import io.github.aquerr.futrzakbot.config.Configuration;
import io.github.aquerr.futrzakbot.events.MessageListener;
import io.github.aquerr.futrzakbot.events.ReadyListener;
import io.github.aquerr.futrzakbot.events.SlashCommandListener;
import io.github.aquerr.futrzakbot.games.GameManager;
import io.github.aquerr.futrzakbot.role.DiscordRoleGiver;
import io.github.aquerr.futrzakbot.role.RoleMessageReactListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FutrzakBot
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakBot.class);

    private final Path botDirectory = Paths.get(".").toAbsolutePath();

    public static void main(String[] args)
    {
        FutrzakBot futrzakBot = new FutrzakBot();
        futrzakBot.start();
    }

    private JDA jda;

    private FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private GameManager gameManager;
    private CommandManager commandManager;
    private Configuration configuration;
    private DiscordRoleGiver discordRoleGiver;

    private void start()
    {
        Configuration configuration = Configuration.loadConfiguration();
        if (configuration.getBotToken().isEmpty())
        {
            throw new IllegalArgumentException("Nie podano tokenu bota. Wpisz brakujący token w pliku konfiguracyjnym.");
        }

        try
        {
            this.configuration = configuration;
            this.futrzakAudioPlayerManager = new FutrzakAudioPlayerManager(this);
            this.gameManager = new GameManager(this);
            this.commandManager = new CommandManager(this);
            this.discordRoleGiver = new DiscordRoleGiver(this);

            this.jda = JDABuilder.createDefault(configuration.getBotToken())
                    .addEventListeners(new MessageListener(this))
                    .addEventListeners(new ReadyListener())
                    .addEventListeners(new SlashCommandListener(this.futrzakAudioPlayerManager))
                    .addEventListeners(new RoleMessageReactListener(this, this.discordRoleGiver))
                    .setAutoReconnect(true)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "FutrzakiShow !f help https://github.com/Aquerr/FutrzakBot"))
                .build().awaitReady();

            this.discordRoleGiver.init();
            this.futrzakAudioPlayerManager.registerAudioPlayersForGuilds(this.jda.getGuilds());

            this.jda.getGuilds().forEach(this::tryToRegisterPlayerGuildSlashCommands);

            LOGGER.info("FutrzakBot Connected!");
        }
        catch (LoginException | InterruptedException e)
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

    public FutrzakAudioPlayerManager getFutrzakAudioPlayerManager()
    {
        return futrzakAudioPlayerManager;
    }

    public Path getBotDirectory()
    {
        return this.botDirectory;
    }

    public CommandManager getCommandManager()
    {
        return this.commandManager;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    private void tryToRegisterPlayerGuildSlashCommands(Guild guild)
    {
        try
        {
            guild.updateCommands()
                    .addCommands(new CommandData("player", "Open player menu")
                            .addOption(OptionType.STRING, "song", "Enter song name to play", false)
                            .setDefaultEnabled(true))
                    .complete();
        }
        catch (Exception exception)
        {
            LOGGER.warn("Slash commands could not be registered for guild '{}'. Reason: {}", guild.getName(), exception.getMessage());
        }
    }
}
