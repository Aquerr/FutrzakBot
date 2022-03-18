package io.github.aquerr.futrzakbot.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.parser.ConfigDocument;
import com.typesafe.config.parser.ConfigDocumentFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Configuration
{
    private final String botToken;
    private final boolean roleGiverEnabled;
    private final long guildId;
    private final long channelId;
    private final long messageId;
    private final Map<String, Long> emoteRoleIdsMap;

    public static Configuration loadConfiguration()
    {
        Config config = loadConfigFile();
        return new Configuration(config);
    }

    public Configuration(Config config)
    {
        this.botToken = config.getString("bot-token");
        this.emoteRoleIdsMap = (Map)config.getConfig("role-giver").getAnyRef("roles");
        this.roleGiverEnabled = config.getConfig("role-giver").getBoolean("enabled");
        this.guildId = config.getConfig("role-giver").getLong("guild-id");
        this.channelId = config.getConfig("role-giver").getLong("channel-id");
        this.messageId = config.getConfig("role-giver").getLong("message-id");
    }

    public String getBotToken()
    {
        return botToken;
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

    private static Config loadConfigFile()
    {
        Path configFilePath = Paths.get(".").resolve("config.conf");
        if (Files.notExists(configFilePath))
        {
            try
            {
                Files.createFile(configFilePath);
                Config defaultClasspathConfig = loadDefaultClasspathConfig();
                String configFileString = defaultClasspathConfig.root().render(ConfigRenderOptions.defaults().setJson(false).setOriginComments(false).setFormatted(true));
                Files.write(configFilePath, configFileString.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return ConfigFactory.parseFile(configFilePath.toFile());
    }

    private static Config loadDefaultClasspathConfig()
    {
        return ConfigFactory.parseResources("config.conf");
    }
}
