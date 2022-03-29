package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.audio.FutrzakAudioPlayerManager;
import io.github.aquerr.futrzakbot.command.ClearCommand;
import io.github.aquerr.futrzakbot.command.CommandManager;
import io.github.aquerr.futrzakbot.command.DebilCommand;
import io.github.aquerr.futrzakbot.command.EightBallCommand;
import io.github.aquerr.futrzakbot.command.FightCommand;
import io.github.aquerr.futrzakbot.command.FutrzakCommand;
import io.github.aquerr.futrzakbot.command.HelpCommand;
import io.github.aquerr.futrzakbot.command.InfoCommand;
import io.github.aquerr.futrzakbot.command.LoopCommand;
import io.github.aquerr.futrzakbot.command.LoveCommand;
import io.github.aquerr.futrzakbot.command.PlayCommand;
import io.github.aquerr.futrzakbot.command.QueueCommand;
import io.github.aquerr.futrzakbot.command.QuoteCommand;
import io.github.aquerr.futrzakbot.command.RemoveComand;
import io.github.aquerr.futrzakbot.command.ResumeCommand;
import io.github.aquerr.futrzakbot.command.RouletteCommand;
import io.github.aquerr.futrzakbot.command.SkipCommand;
import io.github.aquerr.futrzakbot.command.StopCommand;
import io.github.aquerr.futrzakbot.command.VolumeCommand;
import io.github.aquerr.futrzakbot.config.Configuration;
import io.github.aquerr.futrzakbot.events.MessageListener;
import io.github.aquerr.futrzakbot.events.ReadyListener;
import io.github.aquerr.futrzakbot.events.SlashCommandListener;
import io.github.aquerr.futrzakbot.games.GameManager;
import io.github.aquerr.futrzakbot.games.QuoteGame;
import io.github.aquerr.futrzakbot.message.Localization;
import io.github.aquerr.futrzakbot.message.MessageSource;
import io.github.aquerr.futrzakbot.role.DiscordRoleGiver;
import io.github.aquerr.futrzakbot.role.RoleMessageReactListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FutrzakBot
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakBot.class);
    private static final String ERROR_MISSING_BOT_TOKEN = "error.missing-bot-token";
    private static final String COULD_NOT_REGISTER_SLASH_COMMANDS = "error.command.slash.could-not-register";

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
    private MessageSource messageSource;

    private void start()
    {
        this.configuration = Configuration.loadConfiguration();
        this.messageSource = new MessageSource(Localization.forTag(this.configuration.getLanguageTag()));
        if (configuration.getBotToken().isEmpty())
        {
            throw new IllegalArgumentException(this.messageSource.getMessage(ERROR_MISSING_BOT_TOKEN));
        }

        try
        {
            this.futrzakAudioPlayerManager = new FutrzakAudioPlayerManager(this);
            this.gameManager = new GameManager(this);
            this.commandManager = new CommandManager(this.messageSource);
            registerCommands();

            this.jda = JDABuilder.createDefault(configuration.getBotToken())
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .addEventListeners(new MessageListener(this))
                    .addEventListeners(new ReadyListener())
                    .addEventListeners(new SlashCommandListener(this.futrzakAudioPlayerManager))
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
        this.commandManager.registerCommand(new HelpCommand(this.commandManager, this.messageSource));
        this.commandManager.registerCommand(new EightBallCommand());
        this.commandManager.registerCommand(new RouletteCommand());
        this.commandManager.registerCommand(new DebilCommand());
        this.commandManager.registerCommand(new LoveCommand());
        this.commandManager.registerCommand(new FutrzakCommand(this.gameManager.getFutrzakGame()));
        this.commandManager.registerCommand(new PlayCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new StopCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new ResumeCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new VolumeCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new SkipCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new RemoveComand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new ClearCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new QueueCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new InfoCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new LoopCommand(this.futrzakAudioPlayerManager));
        this.commandManager.registerCommand(new FightCommand());
        this.commandManager.registerCommand(new QuoteCommand(QuoteGame.getInstance()));
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
            guild.updateCommands()
                    .addCommands(new CommandData("player", "Open player menu")
                            .addOption(OptionType.STRING, "song", "Enter song name to play", false)
                            .setDefaultEnabled(true))
                    .complete();
        }
        catch (Exception exception)
        {
            LOGGER.warn(messageSource.getMessage(COULD_NOT_REGISTER_SLASH_COMMANDS, guild.getName(), exception.getMessage()));
        }
    }
}
