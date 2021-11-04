package io.github.aquerr.futrzakbot.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configuration
{
    private final String botToken;

    public static Configuration loadConfiguration()
    {
        Config config = ConfigFactory.load("config.conf");
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
}
