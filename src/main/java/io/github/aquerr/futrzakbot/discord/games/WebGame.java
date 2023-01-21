package io.github.aquerr.futrzakbot.discord.games;

import io.github.aquerr.futrzakbot.storage.WebGameRequestStorage;
import io.github.aquerr.futrzakbot.storage.entity.WebGameRequest;
import io.github.aquerr.futrzakbot.util.SpringContextHelper;
import io.github.aquerr.futrzakbot.web.game.service.GameLinkProvider;
import io.github.aquerr.futrzakbot.web.game.service.TokenGenerator;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

public class WebGame
{
    public String requestWebGameForUser(User user)
    {
        String token = SpringContextHelper.getBean(TokenGenerator.class).generate();
        String url = SpringContextHelper.getBean(GameLinkProvider.class).provide(token);
        WebGameRequestStorage webGameRequestStorage = SpringContextHelper.getBean(WebGameRequestStorage.class);
        webGameRequestStorage.saveAndFlush(prepareWebGameRequest(token, user.getIdLong()));
        return url;
    }

    private WebGameRequest prepareWebGameRequest(String token, long userId)
    {
        WebGameRequest webGameRequest = new WebGameRequest();
        webGameRequest.setUserId(userId);
        webGameRequest.setToken(token);
        webGameRequest.setGameName(selectRandomGame());
        webGameRequest.setCreatedDate(OffsetDateTime.now());
        webGameRequest.setExpirationDate(OffsetDateTime.now().plusHours(1));
        return webGameRequest;
    }

    private String selectRandomGame()
    {
        return "breakout";
    }
}
