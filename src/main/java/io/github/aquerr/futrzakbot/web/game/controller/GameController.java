package io.github.aquerr.futrzakbot.web.game.controller;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/game")
public class GameController
{
    @PostMapping(value = "/win", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void confirmWin(@RequestBody WinConfirmation winConfirmation)
    {
        log.info(winConfirmation.toString());
    }

    @Value
    private static class WinConfirmation
    {
        String token;
    }
}
