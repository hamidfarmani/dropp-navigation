package util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kasra on 2/3/2017.
 */
public class IOCContainer {
    private static ApplicationContext IocContainer = new ClassPathXmlApplicationContext("beans.xml");

    public static Object getBean(String beanId) {
        return IocContainer.getBean(beanId);
    }
}
