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

public class Configuration
{
    private final String botToken;

    public static Configuration loadConfiguration()
    {
        Config config = loadConfigFile();
        return new Configuration(config);
    }

    public Configuration(Config config)
    {
        this.botToken = config.getString("bot-token");
    }

    public String getBotToken()
    {
        return botToken;
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
                String configFileString = defaultClasspathConfig.root().render(ConfigRenderOptions.defaults().setJson(false));
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
