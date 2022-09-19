package io.github.aquerr.futrzakbot.command;

import io.github.aquerr.futrzakbot.command.context.CommandContext;
import io.github.aquerr.futrzakbot.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.command.parameters.RemainingStringsParameter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EightBallCommand implements Command, SlashCommand
{
    private static final String QUESTION_PARAM_KEY = "question";

    private static final Random RANDOM = new Random();

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel channel = context.getTextChannel();
        String randomResponse = getRandomResponse(channel);
        channel.sendMessage(randomResponse).queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("8ball");
    }

    @Override
    public String getUsage()
    {
        return CommandManager.COMMAND_PREFIX + " 8ball <question>";
    }

    @Override
    public String getName()
    {
        return ":question: \"Wyrocznia\" odpowie na Twoje pytanie: ";
    }

    @Override
    public String getDescription()
    {
        return "Wyrocznia odpowie na Twoje pytanie";
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(QUESTION_PARAM_KEY).build());
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return new CommandData(getAliases().get(0), getDescription())
                .addOption(OptionType.STRING, "question", "The question for 8ball", true)
                .setDefaultEnabled(true);
    }

    @Override
    public boolean onSlashCommand(SlashCommandEvent event)
    {
        event.deferReply().addEmbeds(new EmbedBuilder()
                .addField("Pytanie:", event.getOption("question").getAsString(), false)
                .addField("Odpowiedź wyroczni:", getRandomResponse(event.getTextChannel()), false)
                .build())
            .queue();
        return true;
    }

    @Override
    public boolean onButtonClick(ButtonClickEvent event)
    {
        return false;
    }

    @Override
    public boolean supports(SlashCommandEvent event)
    {
        return event.getName().equals(getAliases().get(0));
    }

    @Override
    public boolean supports(ButtonClickEvent event)
    {
        return false;
    }

    private String getRandomResponse(TextChannel channel)
    {
        int max = 11;
        int min = 1;
        int i = RANDOM.nextInt(max - min + 1) + min;

        switch (i)
        {
            case 1:
                return "Sądzę że to możliwe.";
            case 2:
                return "Zdecydowanie TAK!";
            case 3:
                List<Member> members = channel.getJDA().getGuildById(channel.getGuild().getId()).getMembers();
                int memberIndex = RANDOM.nextInt(members.size() + 1);
                return "Zapytaj " + members.get(memberIndex).getAsMention() + "!" + " Ta osoba zna odpowiedź.";
            case 4:
                return "Raczej nie..";
            case 5:
                return "Wątpię w to.";
            case 6:
                return "Odpowiedź na to pytanie jest zapisana w gwiazdach.";
            case 7:
                return "Wydaje mi się że tak.";
            case 8:
                return "Przemilczę to pytanie...";
            case 9:
                return "Sory, ale to pytanie jest za trudne na mój mózg.";
            case 10:
                return "Nie licz na to.";
            case 11:
                return "Myślę że odpowiedzią jest 42";
            default:
                return "";
        }
    }
}
