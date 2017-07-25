package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by kasra on 1/31/2017.
 */
@WebListener
public class LocalEntityManagerFactory implements ServletContextListener {
    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    public static EntityManager createEntityManager() {
        if (ENTITY_MANAGER_FACTORY == null) {
            throw new IllegalStateException("Context is not initialized yet");
        }
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("GenoTSWebService");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ENTITY_MANAGER_FACTORY.close();
    }
}

