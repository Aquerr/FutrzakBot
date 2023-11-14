package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.discord.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.discord.command.ClearCommand;
import io.github.aquerr.futrzakbot.discord.command.CommandManager;
import io.github.aquerr.futrzakbot.discord.command.DebilCommand;
import io.github.aquerr.futrzakbot.discord.command.EightBallCommand;
import io.github.aquerr.futrzakbot.discord.command.FightCommand;
import io.github.aquerr.futrzakbot.discord.command.FutrzakCommand;
import io.github.aquerr.futrzakbot.discord.command.GameCommand;
import io.github.aquerr.futrzakbot.discord.command.HelpCommand;
import io.github.aquerr.futrzakbot.discord.command.PlayerCommand;
import io.github.aquerr.futrzakbot.discord.command.LeaveCommand;
import io.github.aquerr.futrzakbot.discord.command.LoopCommand;
import io.github.aquerr.futrzakbot.discord.command.LoveCommand;
import io.github.aquerr.futrzakbot.discord.command.PauseCommand;
import io.github.aquerr.futrzakbot.discord.command.PlayCommand;
import io.github.aquerr.futrzakbot.discord.command.QueueCommand;
import io.github.aquerr.futrzakbot.discord.command.QuoteCommand;
import io.github.aquerr.futrzakbot.discord.command.RemoveCommand;
import io.github.aquerr.futrzakbot.discord.command.ResumeCommand;
import io.github.aquerr.futrzakbot.discord.command.RouletteCommand;
import io.github.aquerr.futrzakbot.discord.command.SkipCommand;
import io.github.aquerr.futrzakbot.discord.command.StopCommand;
import io.github.aquerr.futrzakbot.discord.command.VolumeCommand;
import io.github.aquerr.futrzakbot.discord.config.Configuration;
import io.github.aquerr.futrzakbot.discord.config.JsonPathConfiguration;
import io.github.aquerr.futrzakbot.discord.events.ReadyListener;
import io.github.aquerr.futrzakbot.discord.games.GameManager;
import io.github.aquerr.futrzakbot.discord.games.quote.QuoteGame;
import io.github.aquerr.futrzakbot.discord.listener.DiscordEventListener;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main bot class.
 */
public class FutrzakBot
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakBot.class);
    private static final String ERROR_MISSING_BOT_TOKEN = "error.missing-bot-token";
    private static final String COULD_NOT_REGISTER_SLASH_COMMANDS = "error.command.slash.could-not-register";

    private final Path botDirectory = Paths.get(".").toAbsolutePath();

    private JDA jda;
    private FutrzakAudioPlayerManager futrzakAudioPlayerManager;
    private GameManager gameManager;
    private CommandManager commandManager;
    private final Configuration configuration;
    private JsonPathConfiguration jsonPathConfiguration;
    private DiscordRoleGiver discordRoleGiver;
    private final MessageSource messageSource;
    private FutrzakMessageEmbedFactory messageEmbedFactory;

    FutrzakBot(Configuration configuration, MessageSource messageSource)
    {
        this.configuration = configuration;
        this.messageSource = messageSource;
    }

    void start()
    {
        this.jsonPathConfiguration = new JsonPathConfiguration();
        this.jsonPathConfiguration.configure();
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
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .addEventListeners(this.commandManager)
                    .addEventListeners(new ReadyListener())
                    .addEventListeners(new DiscordEventListener(futrzakAudioPlayerManager))
                    .setAutoReconnect(true)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setActivity(Activity.watching("FutrzakiShow " + CommandManager.COMMAND_PREFIX + " help https://github.com/Aquerr/FutrzakBot"))
                .build().awaitReady();

            initRoleGiver();

            this.futrzakAudioPlayerManager.registerAudioPlayersForGuilds(this.jda.getGuilds());

            this.jda.getGuilds().forEach(this::tryToRegisterPlayerGuildSlashCommands);

            LOGGER.info("FutrzakBot Connected!");
        }
        catch (InterruptedException e)
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
        this.commandManager.registerCommand(new EightBallCommand(this.messageSource));
        this.commandManager.registerCommand(new RouletteCommand());
        this.commandManager.registerCommand(new DebilCommand());
        this.commandManager.registerCommand(new LoveCommand(this.messageSource));
        this.commandManager.registerCommand(new FutrzakCommand(this.gameManager.getFutrzakGame(), this.messageSource));
        this.commandManager.registerCommand(new PlayCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new StopCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new PauseCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new ResumeCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new VolumeCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new SkipCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new RemoveCommand(this.futrzakAudioPlayerManager, this.messageSource));
        this.commandManager.registerCommand(new ClearCommand(this.futrzakAudioPlayerManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new QueueCommand(this.futrzakAudioPlayerManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new PlayerCommand(this.futrzakAudioPlayerManager, this.messageEmbedFactory, this.messageSource));
        this.commandManager.registerCommand(new LoopCommand(this.futrzakAudioPlayerManager, this.messageSource, this.messageEmbedFactory));
        this.commandManager.registerCommand(new FightCommand(this.messageSource));
        this.commandManager.registerCommand(new QuoteCommand(QuoteGame.getInstance(), this.messageSource));
        this.commandManager.registerCommand(new GameCommand(this.messageSource, this.messageEmbedFactory, this.gameManager.getWebGame()));
        this.commandManager.registerCommand(new LeaveCommand(this.messageSource, this.futrzakAudioPlayerManager));
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
