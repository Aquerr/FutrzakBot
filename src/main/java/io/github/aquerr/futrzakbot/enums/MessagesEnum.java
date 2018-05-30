package io.github.aquerr.futrzakbot.enums;

public enum MessagesEnum
{
    COMMANDS("!futrzak komendy"),
    ROULETTE("!futrzak ruletka"),
    EIGHTBALL("!futrzak 8ball"),
    LOVE("!futrzak love"),
    CREATE("!futrzak stworz"),
    FIGHT("!futrzak walka");

    private final String name;

    MessagesEnum(String s)
    {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
