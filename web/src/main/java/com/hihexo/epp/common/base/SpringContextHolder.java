package com.hihexo.epp.common.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @desc SpringContext 容器
 */
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;


    /**
     * 获取bean 信息
     * @param clazz
     * @param <T>
     * @return
     */
    public static final <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取bean 信息
     * @param clazz
     * @param <T>
     * @return
     */
    public static final <T> T getBean(Class<T> clazz,String alias){
        return applicationContext.getBean(alias,clazz);
    }




    @Override
    public void setApplicationContext(ApplicationContext applicationContext1) throws BeansException {
        applicationContext = applicationContext1;
    }
}
