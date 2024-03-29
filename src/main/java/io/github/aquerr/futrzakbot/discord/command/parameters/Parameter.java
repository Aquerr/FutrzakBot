package io.github.aquerr.futrzakbot.discord.command.parameters;

public interface Parameter<T>
{
    String getKey();

    Class<T> getType();

    boolean isOptional();

    static ParameterImpl.ParameterImplBuilder builder()
    {
        return ParameterImpl.builder();
    }
}
