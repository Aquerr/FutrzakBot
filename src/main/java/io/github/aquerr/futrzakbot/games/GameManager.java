package io.github.aquerr.futrzakbot.games;

import io.github.aquerr.futrzakbot.FutrzakBot;

public class GameManager
{
    private final FutrzakBot futrzakBot;
    private final QuoteGame quoteGame;

    public GameManager(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        this.quoteGame = new QuoteGame(futrzakBot);
    }

    public QuoteGame getQuoteGame()
    {
        return quoteGame;
    }
}
