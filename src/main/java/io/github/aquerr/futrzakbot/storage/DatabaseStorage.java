package io.github.aquerr.futrzakbot.storage;

import io.github.aquerr.futrzakbot.storage.provider.FutrzakSessionFactoryProvider;
import org.hibernate.SessionFactory;

public class DatabaseStorage implements Storage
{
    private final SessionFactory sessionFactory;
    public DatabaseStorage()
    {
        this.sessionFactory = FutrzakSessionFactoryProvider.getInstance().getSessionFactory();
    }
}
