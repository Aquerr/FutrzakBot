package io.github.aquerr.futrzakbot;

import io.github.aquerr.futrzakbot.discord.config.Configuration;
import io.github.aquerr.futrzakbot.discord.message.Localization;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Class used for bootstrapping FutrzakBot and starting spring boot.
 */
@SpringBootApplication
public class FutrzakBotBootstrap implements CommandLineRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FutrzakBotBootstrap.class);

    public static void main(String[] args)
    {
        Configuration configuration = Configuration.loadConfiguration();
        MessageSource messageSource = new MessageSource(Localization.forTag(configuration.getLanguageTag()));

        FutrzakBot futrzakBot = new FutrzakBot(configuration, messageSource);
        futrzakBot.start();

        startSpringBoot(configuration, args);
    }

    private static void startSpringBoot(Configuration configuration, String[] args)
    {
        WebApplicationType webApplicationType = WebApplicationType.NONE;
        if (configuration.isWebEnabled())
            webApplicationType = WebApplicationType.SERVLET;

        new SpringApplicationBuilder(FutrzakBotBootstrap.class)
                .web(webApplicationType)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        LOGGER.info("Futrzak x spring integration available!");
    }
}
