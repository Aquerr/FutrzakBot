package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.annotations.BotCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Random;

@BotCommand(argsCount = 1)
public class EightBallCommand implements Command
{
    private static final Random RANDOM = new Random();

    @Override
    public boolean execute(Member member, TextChannel channel, List<String> args)
    {
        int max = 11;
        int min = 1;
        int i = RANDOM.nextInt(max - min + 1) + min;

        switch (i)
        {
            case 1:
                channel.sendMessage("Sądzę że to możliwe.").complete();
                break;
            case 2:
                channel.sendMessage("Zdecydowanie TAK!").complete();
                break;
            case 3:
                List<Member> members = channel.getJDA().getGuildById(channel.getGuild().getId()).getMembers();
                int memberIndex = RANDOM.nextInt(members.size() + 1);
                channel.sendMessage("Zapytaj ").append(members.get(memberIndex).getAsMention()).append("!").append(" Ta osoba zna odpowiedź.").complete();
                break;
            case 4:
                channel.sendMessage("Raczej nie..").complete();
                break;
            case 5:
                channel.sendMessage("Wątpię w to.").complete();
                break;
            case 6:
                channel.sendMessage("Odpowiedź na to pytanie jest zapisana w gwiazdach.").complete();
                break;
            case 7:
                channel.sendMessage("Wydaje mi się że tak.").complete();
                break;
            case 8:
                channel.sendMessage("Przemilczę to pytanie...").complete();
                break;
            case 9:
                channel.sendMessage("Sory, ale to pytanie jest za trudne na mój mózg.").complete();
                break;
            case 10:
                channel.sendMessage("Nie licz na to.").complete();
                break;
            case 11:
                channel.sendMessage("Myślę że odpowiedzią jest 42").complete();
                break;
        }
        return true;
    }

    @Override
    public String getUsage()
    {
        return "!f 8ball <question>";
    }

    @Override
    public String getHelpName()
    {
        return ":question: \"Wyrocznia\" odpowie na Twoje pytanie: ";
    }
}
