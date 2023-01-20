package io.github.aquerr.futrzakbot.storage.provider;

import io.github.aquerr.futrzakbot.storage.entity.MiniGameEncounter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class FutrzakSessionFactoryProvider
{
    private final SessionFactory sessionFactory;

    private static FutrzakSessionFactoryProvider FUTRZAK_SESSION_FACTORY_PROVIDER;

    public static FutrzakSessionFactoryProvider getInstance()
    {
        if (FUTRZAK_SESSION_FACTORY_PROVIDER != null)
            return FUTRZAK_SESSION_FACTORY_PROVIDER;

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources = prepareClasses(metadataSources);
        Metadata metadata = metadataSources.buildMetadata();

        SessionFactory sessionFactory = metadata.buildSessionFactory();
        FUTRZAK_SESSION_FACTORY_PROVIDER = new FutrzakSessionFactoryProvider(sessionFactory);
        return FUTRZAK_SESSION_FACTORY_PROVIDER;
    }

    private static MetadataSources prepareClasses(MetadataSources metadataSources)
    {
        return metadataSources.addPackage("io.github.aquerr.futrzakbot.storage.entity");
    }

    private FutrzakSessionFactoryProvider(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
}
