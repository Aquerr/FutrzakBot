package io.github.aquerr.futrzak.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Roulette
{
    private static final int NUMBER_OF_CHAMBERS = 6;
    private static List<String> chamberList;

    private static boolean isActive;

    private Roulette()
    {

    }

    public static void startNewGame()
    {
        isActive = true;
        chamberList = new ArrayList<>(6);

        for (int i = 0; i < 6; i++)
        {
            chamberList.add("");
        }

        Random random = new Random();
        int index = random.nextInt(NUMBER_OF_CHAMBERS + 1);

        chamberList.add(index, "bullet");
    }

    public static boolean isActive()
    {
        return isActive;
    }

    public static boolean tryShot()
    {
        if (chamberList.get(0).equals("bullet"))
        {
            isActive = false;
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
