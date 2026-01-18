package io.github.aquerr.futrzakbot.discord.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Configuration
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    private static final String CONFIG_FILE_NAME = "config.conf";
    private static final Path DEFAULT_CONFIG_PATH = Paths.get(".").resolve("config").resolve(CONFIG_FILE_NAME);

    private final String botToken;
    private final String youtubeOauthRefreshToken;
    private final String languageTag;
    private final boolean webEnabled;
    private final boolean roleGiverEnabled;
    private final long guildId;
    private final long channelId;
    private final long messageId;
    private final Map<String, Long> emoteRoleIdsMap;

    public static Configuration loadConfiguration()
    {
        Config config = loadConfig();
        return new Configuration(config);
    }

    public Configuration(Config config)
    {
        this.botToken = config.getString("bot-token");
        this.youtubeOauthRefreshToken = config.getConfig("music-player").getConfig("youtube").getConfig("oauth").getString("refresh-token");
        this.webEnabled = config.getBoolean("web-enabled");
        this.emoteRoleIdsMap = (Map)config.getConfig("role-giver").getAnyRef("roles");
        this.roleGiverEnabled = config.getConfig("role-giver").getBoolean("enabled");
        this.guildId = config.getConfig("role-giver").getLong("guild-id");
        this.channelId = config.getConfig("role-giver").getLong("channel-id");
        this.messageId = config.getConfig("role-giver").getLong("message-id");
        this.languageTag = config.getString("lang");
    }

    public String getBotToken()
    {
        return botToken;
    }

    public String getYoutubeOauthRefreshToken()
    {
        return youtubeOauthRefreshToken;
    }

    public String getLanguageTag()
    {
        return languageTag;
    }

    public Map<String, Long> getEmoteRoleIdsMap()
    {
        return emoteRoleIdsMap;
    }

    public long getChannelId()
    {
        return channelId;
    }

    public long getGuildId()
    {
        return guildId;
    }

    public long getMessageId()
    {
        return messageId;
    }

    public boolean isRoleGiverEnabled()
    {
        return roleGiverEnabled;
    }

    public boolean isWebEnabled()
    {
        return webEnabled;
    }

    private static Config loadConfig()
    {
        Path locatedConfig = locateConfigFile().orElse(null);

        LOGGER.info("Configuration file path: {}", locatedConfig);

        if (locatedConfig == null)
        {
            try
            {
                LOGGER.info("Creating default configuration file at: {}", DEFAULT_CONFIG_PATH);
                Files.createFile(DEFAULT_CONFIG_PATH);
                Config defaultClasspathConfig = loadDefaultClasspathConfig();
                String configFileString = defaultClasspathConfig.root().render(ConfigRenderOptions.defaults().setJson(false).setOriginComments(false).setFormatted(true));
                Files.write(DEFAULT_CONFIG_PATH, configFileString.getBytes(StandardCharsets.UTF_8));
                locatedConfig = DEFAULT_CONFIG_PATH;
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not create configuration file.", e);
            }
        }

        LOGGER.info("Loading configuration file: {}", locatedConfig);
        return ConfigFactory.load(ConfigFactory.systemEnvironment().withFallback(ConfigFactory.parseFile(locatedConfig.toFile())));
    }

    private static Optional<Path> locateConfigFile()
    {
        List<Path> possibleConfigPaths = List.of(
                DEFAULT_CONFIG_PATH,
                Paths.get(".").resolve(CONFIG_FILE_NAME)
        );

        for (Path path : possibleConfigPaths)
        {
            if (Files.exists(path))
                return Optional.of(path);
        }
        return Optional.empty();
    }

    private static Config loadDefaultClasspathConfig()
    {
        return ConfigFactory.load(ConfigFactory.parseResources(CONFIG_FILE_NAME));
    }
}
