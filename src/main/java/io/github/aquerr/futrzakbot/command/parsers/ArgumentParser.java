package io.github.aquerr.futrzakbot.command.parsers;

public interface ArgumentParser<T>
{
    T parse(String input);
}
