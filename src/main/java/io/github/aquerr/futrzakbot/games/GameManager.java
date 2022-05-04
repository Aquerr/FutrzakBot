package io.github.aquerr.futrzakbot.games;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.games.quote.QuoteGame;

public class GameManager
{
    private final FutrzakBot futrzakBot;
    private final QuoteGame quoteGame;
    private final FutrzakGame futrzakGame;
    private final ValheimGame valheimGame;

    public GameManager(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        this.quoteGame = QuoteGame.getInstance();
        this.futrzakGame = new FutrzakGame(futrzakBot);
        this.futrzakGame.setup();
        this.valheimGame = new ValheimGame(futrzakBot.getConfiguration().getValheimServerIp());
    }

    public QuoteGame getQuoteGame()
    {
        return quoteGame;
    }

    public FutrzakGame getFutrzakGame()
    {
        return futrzakGame;
    }

    public ValheimGame getValheimGame()
    {
        return valheimGame;
    }
}
