package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.*;
import io.github.aquerr.futrzakbot.discord.command.*;
import io.github.aquerr.futrzakbot.discord.config.Configuration;
import io.github.aquerr.futrzakbot.discord.config.JsonPathConfiguration;
import io.github.aquerr.futrzakbot.discord.events.MessageListener;
import io.github.aquerr.futrzakbot.discord.events.ReadyListener;
import io.github.aquerr.futrzakbot.discord.events.SlashCommandListener;
import io.github.aquerr.futrzakbot.discord.games.GameManager;
import io.github.aquerr.futrzakbot.discord.games.quote.QuoteGame;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.Localization;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import io.github.aquerr.futrzakbot.discord.role.DiscordRoleGiver;
import io.github.aquerr.futrzakbot.discord.role.RoleMessageReactListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class FutrzakBot implements CommandLineRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakBot.class);
    private static final String ERROR_MISSING_BOT_TOKEN = "error.missing-bot-token";
    private static final String COULD_NOT_REGISTER_SLASH_COMMANDS = "error.command.slash.could-not-register";

    private final Path botDirectory = Paths.get(".").toAbsolutePath();

    public static void main(String[] args)
    {
        FutrzakBot futrzakBot = new FutrzakBot();
        futrzakBot.start();

        new SpringApplicationBuilder(FutrzakBot.class)
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        LOGGER.info("Helo from Spring Command Line Runner!");
    }

    private JDA jda;
    private FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private GameManager gameManager;
    private CommandManager commandManager;
    private Configuration configuration;
    private JsonPathConfiguration jsonPathConfiguration;
    private DiscordRoleGiver discordRoleGiver;
    private MessageSource messageSource;
    private FutrzakMessageEmbedFactory messageEmbedFactory;

    private void start()
    {
        this.configuration = Configuration.loadConfiguration();
        this.jsonPathConfiguration = new JsonPathConfiguration();
        this.jsonPathConfiguration.configure();
        this.messageSource = new MessageSource(Localization.forTag(this.configuration.getLanguageTag()));
        FutrzakMessageEmbedFactory.init(messageSource);
        this.messageEmbedFactory = FutrzakMessageEmbedFactory.getInstance();
        if (configuration.getBotToken().isEmpty())
        {
            throw new IllegalArgumentException(this.messageSource.getMessage(ERROR_MISSING_BOT_TOKEN));
        }

        try
        {
            this.futrzakAudioPlayerManager = new FutrzakAudioPlayerManager(this, this.messageEmbedFactory);
            this.gameManager = new GameManager(this);
            this.commandManager = new CommandManager(this.messageSource);
            registerCommands();

            this.jda = JDABuilder.createDefault(configuration.getBotToken())
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .addEventListeners(new MessageListener(this, this.messageEmbedFactory))
                    .addEventListeners(new ReadyListener())
                    .addEventListeners(new SlashCommandListener(this.commandManager, this.futrzakAudioPlayerManager, this.messageEmbedFactory))
                    .setAutoReconnect(true)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "FutrzakiShow " + CommandManager.COMMAND_PREFIX + " help https://github.com/Aquerr/FutrzakBot"))
                .build().awaitReady();

            initRoleGiver();

            this.futrzakAudioPlayerManager.registerAudioPlayersForGuilds(this.jda.getGuilds());

            this.jda.getGuilds().forEach(this::tryToRegisterPlayerGuildSlashCommands);

            LOGGER.info("FutrzakBot Connected!");
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void initRoleGiver()
    {
        if (this.configuration.isRoleGiverEnabled())
        {
            this.discordRoleGiver = new DiscordRoleGiver(this);
            this.jda.addEventListener(new RoleMessageReactListener(this, this.discordRoleGiver));
            this.discordRoleGiver.init();
        }
    }

    private void registerCommands()
    {
        this.commandManager.registerCommand(new HelpCommand(this.commandManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new EightBallCommand());
        this.commandManager.registerCommand(new RouletteCommand());
        this.commandManager.registerCommand(new DebilCommand());
        this.commandManager.registerCommand(new LoveCommand());
        this.commandManager.registerCommand(new FutrzakCommand(this.gameManager.getFutrzakGame()));
        this.commandManager.registerCommand(new PlayCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new StopCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new ResumeCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new VolumeCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new SkipCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new RemoveComand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new ClearCommand(this.futrzakAudioPlayerManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new QueueCommand(this.futrzakAudioPlayerManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new InfoCommand(this.futrzakAudioPlayerManager, this.messageEmbedFactory));
        this.commandManager.registerCommand(new LoopCommand(this.futrzakAudioPlayerManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new FightCommand());
        this.commandManager.registerCommand(new QuoteCommand(QuoteGame.getInstance(), this.messageSource));
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

    public MessageSource getMessageSource()
    {
        return messageSource;
    }

    private void tryToRegisterPlayerGuildSlashCommands(Guild guild)
    {
        try
        {
            this.commandManager.registerSlashCommandsForGuild(guild);
        }
        catch (Exception exception)
        {
            LOGGER.warn(messageSource.getMessage(COULD_NOT_REGISTER_SLASH_COMMANDS, guild.getName(), exception.getMessage()));
        }
    }
}
