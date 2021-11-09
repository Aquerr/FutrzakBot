package io.github.aquerr.futrzakbot.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface BotCommand
{
    short argsCount() default 0;
}
