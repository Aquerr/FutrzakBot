package io.github.aquerr.futrzakbot.discord.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Roulette
{
    private final int NUMBER_OF_CHAMBERS = 6;
    private List<String> chamberList;

    public Roulette()
    {
        startNewGame();
    }

    private void startNewGame()
    {
        chamberList = new ArrayList<>(6);

        for (int i = 0; i < 6; i++)
        {
            chamberList.add("");
        }

        Random random = new Random();
        int index = random.nextInt(NUMBER_OF_CHAMBERS + 1);

        chamberList.add(index, "bullet");
    }

    public boolean usePistol()
    {
        if (chamberList.get(0).equals("bullet"))
        {
            return true;
        }
        else
        {
            String emptyChamber = chamberList.remove(0);
            chamberList.add(emptyChamber);

            return false;
        }
    }
}
