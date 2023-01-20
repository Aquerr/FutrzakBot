package io.github.aquerr.futrzakbot.discord.command.context;

import lombok.Builder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder
public class CommandContextImpl implements CommandContext
{
    private final Map<String, Object> parameters;
    private final TextChannel textChannel;
    private final Member member;

    @Override
    public <T> Optional<T> get(String key)
    {
        return Optional.ofNullable((T)this.parameters.get(key));
    }

    @Override
    public <T> T require(String key)
    {
        Object value = this.parameters.get(key);
        if (value == null)
            throw new IllegalStateException("Value for the key '" + key + "' does not exist!");
        return (T)value;
    }

    @Override
    public TextChannel getTextChannel()
    {
        return this.textChannel;
    }

    @Override
    public Member getMember()
    {
        return this.member;
    }

    public static class CommandContextImplBuilder
    {
        private Map<String, Object> parameters = new HashMap<>();

        public CommandContextImplBuilder put(String key, Object parsedArgument)
        {
            if (this.parameters.get(key) != null)
                throw new IllegalArgumentException("Given key '" + key + "' is already occupied in this CommandContext.");

            this.parameters.put(key, parsedArgument);
            return this;
        }

        public CommandContextImplBuilder putAll(Map<String, Object> parameters)
        {
            parameters.forEach(this::put);
            return this;
        }
    }
}
