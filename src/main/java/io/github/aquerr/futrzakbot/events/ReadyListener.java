package io.github.aquerr.futrzakbot.events;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter
{
    @Override
    public void onReady(ReadyEvent event)
    {
        System.out.println("API is ready!");
    }
}
