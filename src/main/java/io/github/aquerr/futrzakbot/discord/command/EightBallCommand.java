package io.github.aquerr.futrzakbot.discord.command;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.aquerr.futrzakbot.discord.command.context.CommandContext;
import io.github.aquerr.futrzakbot.discord.command.parameters.Parameter;
import io.github.aquerr.futrzakbot.discord.command.parameters.RemainingStringsParameter;
import io.github.aquerr.futrzakbot.discord.message.FutrzakMessageEmbedFactory;
import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import lombok.Value;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EightBallCommand implements Command, SlashCommand
{
    private static final Cache<MemberIdWithQuestion, String> EIGHT_BALL_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    private static final String QUESTION_PARAM_KEY = "question";

    private static final Random RANDOM = new Random();

    private final MessageSource messageSource;

    public EightBallCommand(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    @Override
    public boolean execute(CommandContext context)
    {
        TextChannel channel = context.getTextChannel();
        String question = context.require(QUESTION_PARAM_KEY);
        String randomResponse = getRandomResponse(channel, context.getMember(), question.toLowerCase());
        channel.sendMessage(randomResponse).queue();
        return true;
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("8ball");
    }

    @Override
    public String getName()
    {
        return messageSource.getMessage("command.eightball.name");
    }

    @Override
    public String getDescription()
    {
        return messageSource.getMessage("command.eightball.description");
    }

    @Override
    public List<Parameter<?>> getParameters()
    {
        return Collections.singletonList(RemainingStringsParameter.builder().key(QUESTION_PARAM_KEY).build());
    }

    @Override
    public SlashCommandData getSlashCommandData()
    {
        return Commands.slash(getAliases().get(0), getDescription())
                .addOption(OptionType.STRING, "question", messageSource.getMessage("command.eightball.slash.param.question.desc"), true)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event)
    {
        final String question = event.getOption("question").getAsString();

        event.deferReply().addEmbeds(new EmbedBuilder()
                .setColor(FutrzakMessageEmbedFactory.DEFAULT_COLOR)
                .addField(messageSource.getMessage("command.eightball.answer.question"), question, false)
                .addField(messageSource.getMessage("command.eightball.answer.answer"), getRandomResponse(event.getChannel().asTextChannel(), event.getMember(), question.toLowerCase()), false)
                .build())
            .queue();
    }

    private String getRandomResponse(TextChannel channel, Member member, String question)
    {
        MemberIdWithQuestion memberIdWithQuestion = new MemberIdWithQuestion(member.getIdLong(), question);
        String answer = EIGHT_BALL_CACHE.getIfPresent(memberIdWithQuestion);
        if (answer == null)
        {
            answer = getRandomResponse(channel);
            EIGHT_BALL_CACHE.put(memberIdWithQuestion, answer);
        }
        return answer;
    }

    private String getRandomResponse(TextChannel channel)
    {
        int max = 12;
        int min = 1;
        int i = RANDOM.nextInt(max - min + 1) + min;

        switch (i)
        {
            case 1:
                return "Sądzę że to możliwe.";
            case 2:
                return "Zdecydowanie TAK!";
            case 3:
            {
                List<Member> members = channel.getJDA().getGuildById(channel.getGuild().getId()).getMembers();
                int memberIndex = RANDOM.nextInt(members.size() + 1);
                return "Zapytaj " + members.get(memberIndex).getAsMention() + "!" + " Ta osoba zna odpowiedź.";
            }
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
            case 12:
            {
                List<Member> members = channel.getJDA().getGuildById(channel.getGuild().getId()).getMembers();
                int memberIndex = RANDOM.nextInt(members.size() + 1);
                return "Czemu nie zapytasz " + members.get(memberIndex).getAsMention() + "? Ta osoba może coś o tym wiedzieć...";
            }
            default:
                return "";
        }
    }

    @Value
    private static class MemberIdWithQuestion
    {
        long memberId;
        String question;
    }
}
