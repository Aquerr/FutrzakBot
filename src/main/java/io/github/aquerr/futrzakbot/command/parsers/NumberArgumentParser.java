package io.github.aquerr.futrzakbot.command.parsers;

import java.util.PrimitiveIterator;

public class NumberArgumentParser implements ArgumentParser<Integer>
{
    public Integer parse(String input)
    {
        PrimitiveIterator.OfInt iterator = input.chars().iterator();
        StringBuilder number = new StringBuilder();
        while(iterator.hasNext())
        {
            int character = iterator.nextInt();
            if(character >= 48 && character <= 57)
            {
                number.append((char) character);
            }
            else
            {
                break;
            }
        }
        return Integer.valueOf(number.toString());
    }
}
