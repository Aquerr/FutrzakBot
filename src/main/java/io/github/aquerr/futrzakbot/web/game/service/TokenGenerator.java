package io.github.aquerr.futrzakbot.web.game.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TokenGenerator
{
    private static final char[] CHARACTERS = "0123456789abcdefghijklmnoperstuwxyz".toCharArray();
    private static final int TOKEN_LENGTH = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generate()
    {
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < TOKEN_LENGTH; i++)
        {
            char character = CHARACTERS[SECURE_RANDOM.nextInt(CHARACTERS.length)];
            tokenBuilder.append(character);
        }
        return tokenBuilder.toString();
    }
}
