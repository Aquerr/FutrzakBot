package io.github.aquerr.futrzakbot.games;

import io.github.aquerr.futrzakbot.FutrzakBot;

public class GameManager
{
    private final FutrzakBot futrzakBot;
    private final QuoteGame quoteGame;
    private final FutrzakGame futrzakGame;

    public GameManager(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        this.quoteGame = QuoteGame.getInstance();
        this.futrzakGame = new FutrzakGame(futrzakBot);
        this.futrzakGame.setup();
    }

    public QuoteGame getQuoteGame()
    {
        return quoteGame;
    }

    public FutrzakGame getFutrzakGame()
    {
        return futrzakGame;
    }
}
