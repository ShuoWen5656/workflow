package top.swzhao.project.workflow.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author swzhao
 * @date 2023/11/6 10:19 下午
 * @Discreption <>
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static  <T> Map<String, T> getBeansFromClazz(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
