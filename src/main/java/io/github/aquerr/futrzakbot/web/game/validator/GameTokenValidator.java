package io.github.aquerr.futrzakbot.web.game.validator;

import io.github.aquerr.futrzakbot.storage.WebGameRequestStorage;
import io.github.aquerr.futrzakbot.storage.entity.WebGameRequest;
import io.github.aquerr.futrzakbot.util.StringUtils;
import io.github.aquerr.futrzakbot.web.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@AllArgsConstructor
public class GameTokenValidator
{
    final WebGameRequestStorage gameRequestStorage;

    public void validate(String token)
    {
        if (StringUtils.isBlank(token))
            throw new InvalidTokenException();
        WebGameRequest webGameRequest = gameRequestStorage.findByToken(token);
        if (webGameRequest == null)
            throw new InvalidTokenException();
        if (webGameRequest.getExpirationDate().isBefore(OffsetDateTime.now()))
            throw new InvalidTokenException();
    }
}
