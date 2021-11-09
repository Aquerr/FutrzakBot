package io.github.aquerr.futrzakbot.command.parsers;

import java.util.Iterator;
import java.util.PrimitiveIterator;

public class NumberArgumentParser
{
    public static String parse(StringBuilder input)
    {
        Iterator iterator = input.chars().iterator();
        StringBuilder number = new StringBuilder();

        while(iterator.hasNext())
        {
            int character = ((PrimitiveIterator.OfInt) iterator).nextInt();
            if(character >= 48 && character <= 57)
            {
                number.append((char) character);
            }
            else
            {
                break;
            }
        }

        input.delete(0, number.length());

        return number.toString();
    }
}
