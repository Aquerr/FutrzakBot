package io.github.aquerr.futrzakbot.web.game.controller;

import io.github.aquerr.futrzakbot.storage.WebGameRequestStorage;
import io.github.aquerr.futrzakbot.web.exception.InvalidTokenException;
import io.github.aquerr.futrzakbot.web.game.validator.GameTokenValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
public class GameController
{
    private final GameTokenValidator gameTokenValidator;
    private final WebGameRequestStorage webGameRequestStorage;

    @GetMapping("/game")
    public String game(@RequestParam("token") String token)
    {
        gameTokenValidator.validate(token);
        String gameName = webGameRequestStorage.findByToken(token).getGameName();

        return "redirect:/game/{gameName}?token={token}"
                .replace("{gameName}", gameName)
                .replace("{token}", token);
    }

    @GetMapping("/game/thanks-for-playing")
    public ModelAndView thanksForPlaying()
    {
        return new ModelAndView("/game/thanks-for-playing");
    }

    @GetMapping("/game/breakout")
    public ModelAndView breakout()
    {
        return new ModelAndView("/game/breakout");
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Provided game token is invalid")
    public String handleException(InvalidTokenException exception)
    {
        return "redirect:/thanks-for-playing";
    }
}
