package io.github.aquerr.futrzakbot.web.game.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/game")
@AllArgsConstructor
public class GameController
{
    @PostMapping(value = "/win", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void confirmWin(@RequestBody WinConfirmation winConfirmation)
    {
        log.info(winConfirmation.toString());
    }

    @Data
    private static class WinConfirmation
    {
        String token;
    }
}
