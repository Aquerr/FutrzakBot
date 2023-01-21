package io.github.aquerr.futrzakbot.util;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Allows getting spring beans with static methods.
 *
 * Mostly used by code that is not yet managed by spring.
 */
@Component
@AllArgsConstructor
public class SpringContextHelper implements ApplicationContextAware
{
    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        CONTEXT = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass)
    {
        return (T)CONTEXT.getBean(beanClass);
    }

    public static <T> T getBean(String beanName)
    {
        return (T)CONTEXT.getBean(beanName);
    }
}
