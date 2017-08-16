package util;

import model.enums.City;
import model.enums.Gender;
import service.AdminService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Date;

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
        AdminService adminService = (AdminService) IOCContainer.getBean("adminService");
        if(!adminService.isAdminExist()) {
            adminService.adminRegister("admin", "admin", new Date(), "admin@admin.com", "09000000000", "1234567890", "admin", "admin", Gender.MALE, City.TEHRAN);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ENTITY_MANAGER_FACTORY.close();
    }
}

