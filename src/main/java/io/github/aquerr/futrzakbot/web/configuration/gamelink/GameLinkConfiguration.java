package io.github.aquerr.futrzakbot.web.configuration.gamelink;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "futrzak.gamelink")
public class GameLinkConfiguration
{
    private String url;
}
