package io.github.aquerr.futrzakbot.games;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Random;

public class LoveMeter
{
    public static Message checkLove(Member requester, Member selectedMember)
    {
        if (selectedMember != null)
        {
            String mentionedMemberString = selectedMember.getAsMention();
            Random random = new Random();
            int max = 11;
            int min = 1;
            int i = random.nextInt(max - min + 1) + min;

            switch (i)
            {
                case 1:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " są tylko 2% miłości. :broken_heart: Z tego nic nie będzie. Wywal to sobie z głowy.").build();
                case 2:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest tylko 11% miłości. :disappointed: Marne szanse na cokolwiek. :disappointed:").build();
                case 3:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 27% miłości! :heart: Słabo, ale szczypta szansy jest!").build();
                case 4:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 34% miłości! :heart: Nie jest źle, mogło być gorzej! :stuck_out_tongue:").build();
                case 5:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 45% miłości! :heart: Szansa jest, ale czy coś z tego będzie?").build();
                case 6:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 52% miłości! :heart: Nie mam pewności co z tego wyjdzie :thinking:").build();
                case 7:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 61% miłości! :heart:").build();
                case 8:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 76% miłości! :heart: Może coś z tego będzie! :D").build();
                case 9:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 83% miłości! :heart: Jest bardzo dobrze!").build();
                case 10:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 91% miłości! :heart: To może się udać! :heart:").build();
                case 11:
                    return new MessageBuilder("Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 100% miłości!!! :heart: To prawdziwa miłość na zabój!!").build();
            }

            return new MessageBuilder("Coś mi nie pykło w obliczeniach :persevere:").build();
        }
        else
        {
            return new MessageBuilder("Musisz oznaczyć jakąś osobę żebym wiedział z kim sprawdzić Twój poziom miłości :heart:").build();
        }
    }
}
