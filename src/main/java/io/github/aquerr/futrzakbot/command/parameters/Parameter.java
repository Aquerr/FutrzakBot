package io.github.aquerr.futrzakbot.command.parameters;

public interface Parameter<T>
{
    String getKey();

    Class<T> getType();

    static ParameterImpl.ParameterImplBuilder builder()
    {
        return new ParameterImpl.ParameterImplBuilder();
    }
}
