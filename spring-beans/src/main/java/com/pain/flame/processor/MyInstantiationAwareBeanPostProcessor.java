package com.pain.flame.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

/**
 * InstantiationAwareBeanPostProcessor 是 BeanPostProcessor 的子接口
 * Spring 提供了适配类 InstantiationAwareBeanPostProcessorAdapter
 */
public class MyInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    /**
     * 实例化 bean 之前调用
     * 如果此方法返回空，则 bean 创建过程将会短路
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if ("car".equals(beanName)) {
            System.out.println("=== 调用 bean 后处理器的前置方法，postProcessBeforeInstantiation()");
        }

        return null;
    }

    /**
     * 实例化 bean 之后调用，此时 bean 实例还没有进行属性填充
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if ("car".equals(beanName)) {
            System.out.println("=== 调用 bean 后处理器的后置方法，postProcessAfterInstantiation()");
        }

        return true;
    }
}
