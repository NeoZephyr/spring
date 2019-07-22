package com.pain.flame.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Car implements ApplicationContextAware, BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {
    private String brand;
    private String color;
    private int speed;

    public Car() {
        System.out.println("=== 调用 bean 构造方法，car()");
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        System.out.println("=== 调用 bean 属性设置方法，setBrand()");
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        System.out.println("=== 调用 bean 属性设置方法，setColor()");
        this.color = color;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        System.out.println("=== 调用 bean 属性设置方法，setSpeed()");
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", speed=" + speed +
                '}';
    }

    private ApplicationContext context;
    private BeanFactory beanFactory;
    private String beanName;

    /**
     * BeanNameAware 接口
     * 设置 beanName
     * @param s
     */
    public void setBeanName(String s) {
        System.out.println("=== 调用 bean 名称设置方法，setBeanName()");
        this.beanName = s;
    }

    /**
     * BeanFactoryAware 接口
     * 设置 beanFactory
     * @param beanFactory
     * @throws BeansException
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("=== 调用 bean 工厂设置方法，setBeanFactory()");
        this.beanFactory = beanFactory;
    }

    /**
     * InitializingBean 接口
     * afterPropertiesSet
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        System.out.println("=== 调用 bean 属性设置结束后置方法，afterPropertiesSet()");
    }

    /**
     * init-method 属性指定的初始化方法
     */
    public void customInit() {
        System.out.println("=== 调用 init-method 指定方法，customInit()");
    }

    /**
     * DisposableBean 接口
     * 对于 scope="singleton" 的 Bean，当容器关闭时，触发对 Bean 的后续管理工作
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        System.out.println("=== 调用 bean 销毁方法，destroy()");
    }

    /**
     * destroy-method 属性指定的销毁方法
     * 进行资源释放等
     */
    public void customDestroy() {
        System.out.println("=== 调用 destroy-method 指定方法，customDestroy()");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("=== 调用 ApplicationContext 设置方法，setApplicationContext()");
        this.context = applicationContext;
    }
}
