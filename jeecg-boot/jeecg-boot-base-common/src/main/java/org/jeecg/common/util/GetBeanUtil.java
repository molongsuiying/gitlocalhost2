package org.jeecg.common.util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * GetBeanUtil
 *
 * @version: 1.0
 */

@Component
public class GetBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext context) {
        GetBeanUtil .applicationContext = context;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
