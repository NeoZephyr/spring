package com.pain.flame.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    /**
     * 此时 bean 已经进行了属性的填充
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if ("car".equals(beanName)) {
            System.out.println("=== 调用 bean 后处理器的前置方法，postProcessBeforeInitialization()");
        }

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("car".equals(beanName)) {
            System.out.println("=== 调用 bean 后处理器的后置方法，postProcessAfterInitialization()");
        }

        return bean;
    }
}
