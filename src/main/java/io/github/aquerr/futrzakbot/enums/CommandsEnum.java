package io.github.aquerr.futrzakbot.enums;

public enum CommandsEnum
{
    COMMANDS("!futrzak komendy"),
    ROULETTE("!futrzak ruletka"),
    EIGHTBALL("!futrzak 8ball"),
    LOVE("!futrzak love"),
    CREATE("!futrzak stworz"),
    FIGHT("!futrzak walka"),
    QUOTE("!futrzak cytat"),
    DISPLAY("!futrzak futrzak"),
    PLAY("!futrzak play"),
    NEXT("!futrzak next"),
    STOP("!futrzak stop");

    private final String name;

    CommandsEnum(String s)
    {
        name = s;
    }

//    public boolean equalsName(String otherName) {
//        return name.equals(otherName);
//    }

    public String toString() {
        return this.name;
    }
}
