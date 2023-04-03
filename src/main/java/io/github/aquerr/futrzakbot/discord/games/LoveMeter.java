package io.github.aquerr.futrzakbot.discord.games;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Value;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.time.Duration;
import java.util.Random;

public class LoveMeter
{
    private static final Cache<Lovers, MessageEditData> LOVE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    public static MessageEditData checkLove(Member requester, Member selectedMember)
    {
        Lovers lovers = new Lovers(requester.getIdLong(), selectedMember.getIdLong());
        MessageEditData message = LOVE_CACHE.getIfPresent(lovers);
        if (message == null)
        {
            message = getLoveMessage(requester, selectedMember);
            LOVE_CACHE.put(lovers, message);
        }
        return message;
    }

    private static MessageEditData getLoveMessage(Member requester, Member selectedMember)
    {
        if (selectedMember != null)
        {
            String mentionedMemberString = selectedMember.getAsMention();
            Random random = new Random();
            int max = 11;
            int min = 1;
            int i = random.nextInt(max - min + 1) + min;

            String messageContent = null;

            switch (i)
            {
                case 1:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " są tylko 2% miłości. :broken_heart: Z tego nic nie będzie. Wywal to sobie z głowy.";
                    break;
                case 2:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest tylko 11% miłości. :disappointed: Marne szanse na cokolwiek. :disappointed:";
                    break;
                case 3:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 27% miłości! :heart: Słabo, ale szczypta szansy jest!";
                    break;
                case 4:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 34% miłości! :heart: Nie jest źle, mogło być gorzej! :stuck_out_tongue:";
                    break;
                case 5:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 45% miłości! :heart: Szansa jest, ale czy coś z tego będzie?";
                    break;
                case 6:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 52% miłości! :heart: Nie mam pewności co z tego wyjdzie :thinking:";
                    break;
                case 7:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 61% miłości! :heart:";
                    break;
                case 8:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 76% miłości! :heart: Może coś z tego będzie! :D";
                    break;
                case 9:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 83% miłości! :heart: Jest bardzo dobrze!";
                    break;
                case 10:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 91% miłości! :heart: To może się udać! :heart:";
                    break;
                case 11:
                    messageContent = "Między " + requester.getAsMention() + " a " + mentionedMemberString + " jest 100% miłości!!! :heart: To prawdziwa miłość na zabój!!";
                    break;
                default:
                    messageContent = "Coś mi nie pykło w obliczeniach :persevere:";

            }

            return new MessageEditBuilder()
                    .setContent(messageContent)
                    .build();
        }
        else
        {
            return new MessageEditBuilder()
                    .setContent("Musisz oznaczyć jakąś osobę żebym wiedział z kim sprawdzić Twój poziom miłości :heart:")
                    .build();
        }
    }

    @Value
    private static class Lovers
    {
        long firstLoverMemberId;
        long secondLoverMemberId;
    }
}
