package io.github.aquerr.futrzakbot.games;

import java.util.HashMap;
import java.util.Map;

public class RouletteGame
{
    private static Map<Long, Roulette> rouletteMap = new HashMap<>();

    private RouletteGame()
    {

    }

    public static void startNewGame(long guildId)
    {
        Roulette roulette = new Roulette();

        rouletteMap.put(guildId, roulette);
    }

    public static boolean isActive(long guildId)
    {
        return rouletteMap.containsKey(guildId);
    }

    public static boolean usePistol(long guildId)
    {
        boolean didShoot = rouletteMap.get(guildId).usePistol();

        if (didShoot)
        {
            rouletteMap.remove(guildId);
        }

        return didShoot;
    }

}
