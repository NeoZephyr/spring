package com.pain.flame.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * bean 工厂后处理器
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    /**
     * 此时所有 bean 定义信息已经加载，但是还都没有实例化
     * @param beanFactory
     * @throws BeansException
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("car");
        beanDefinition.getPropertyValues().addPropertyValue("brand", "QQ");
        System.out.println("=== 调用 bean 工厂后处理器，postProcessBeanFactory()");
    }
}
