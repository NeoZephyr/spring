package com.pain.flame;

import com.pain.flame.bean.Car;
import com.pain.flame.bean.Phone;
import com.pain.flame.processor.MyBeanFactoryPostProcessor;
import com.pain.flame.processor.MyBeanPostProcessor;
import com.pain.flame.processor.MyInstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.pain.flame.bean"})
public class BeanConfiguration {
    public BeanConfiguration() {
        System.out.println("Init BeanConfiguration...");
    }

    @Bean
    Phone phone() {
        Phone phone = new Phone();
        phone.setBrand("XiaoMi");
        phone.setColor("Black");
        phone.setCores(6);
        return phone;
    }

    @Bean
    Car car() {
        Car car = new Car();
        car.setBrand("QQ");
        car.setColor("Red");
        car.setSpeed(100);

        return car;
    }

    @Bean
    static MyBeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new MyBeanFactoryPostProcessor();
    }

    @Bean
    static MyInstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor() {
        return new MyInstantiationAwareBeanPostProcessor();
    }

    @Bean
    static MyBeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
