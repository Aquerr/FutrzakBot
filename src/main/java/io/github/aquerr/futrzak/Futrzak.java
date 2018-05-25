package io.github.aquerr.futrzak;

import io.github.aquerr.futrzak.events.MessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class Futrzak
{
    public static void main(String[] args)
    {
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT).setToken("NDQ3MTEwNzI0OTI2MzA4MzUz.DeC-mg.fnpB19tWIQlz1iD8eAg8fFZ0upk").buildBlocking();
            System.out.println("Connected!");
            jda.addEventListener(new MessageListener());
            jda.setAutoReconnect(true);
        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
