package io.github.aquerr.futrzakbot.web.game.service;

import io.github.aquerr.futrzakbot.web.configuration.gamelink.GameLinkConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GameLinkProvider
{
    private GameLinkConfiguration gameLinkConfiguration;

    public String provide(String token)
    {
        String url = gameLinkConfiguration.getUrl();
        return url.replace("{TOKEN}", token);
    }
}
