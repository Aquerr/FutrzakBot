package io.github.aquerr.futrzakbot.games;

import java.util.*;

public class RouletteGame
{
    private static Map<String, Roulette> rouletteMap = new HashMap();

    private RouletteGame()
    {

    }

    public static void startNewGame(String guildId)
    {
        Roulette roulette = new Roulette();

        rouletteMap.put(guildId, roulette);
    }

    public static boolean isActive(String guildId)
    {
        return rouletteMap.containsKey(guildId);
    }

    public static boolean usePistol(String guildId)
    {
        boolean didShoot = rouletteMap.get(guildId).usePistol();

        if (didShoot)
        {
            rouletteMap.remove(guildId);
        }

        return didShoot;
    }

}
