package top.swzhao.project.workflow.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author swzhao
 * @date 2023/11/6 10:19 下午
 * @Discreption <>
 */
public class SpringUtils implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public <T> Map<String, T> getBeansFromClazz(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
