package com.pain.flame;

import com.pain.flame.bean.Car;
import com.pain.flame.bean.Phone;
import com.pain.flame.processor.MyBeanPostProcessor;
import com.pain.flame.processor.MyInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class ApplicationContextTest {

    public void testDefaultListableBeanFactory() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:beans.xml");
        System.out.println(resource.getURL());
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(resource);

        Phone phone = factory.getBean("phone", Phone.class);
        System.out.println(phone);

        Car car = factory.getBean("car", Car.class);
        System.out.println(car);
    }

    public void testDefaultListableBeanFactoryWithPostProcessor() {
        Resource resource = new ClassPathResource("beans.xml");
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((DefaultListableBeanFactory) beanFactory);
        reader.loadBeanDefinitions(resource);

        // 注册后处理器
        ((ConfigurableBeanFactory) beanFactory).addBeanPostProcessor(new MyBeanPostProcessor());
        ((ConfigurableBeanFactory) beanFactory).addBeanPostProcessor(new MyInstantiationAwareBeanPostProcessor());

        Phone phone = beanFactory.getBean("phone", Phone.class);
        System.out.println(phone);

        Car car = beanFactory.getBean("car", Car.class);
        System.out.println(car);

        ((DefaultListableBeanFactory) beanFactory).destroySingletons();
    }

    public void testXmlApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
        Phone phone = context.getBean("phone", Phone.class);
        System.out.println(phone);
        context.close();
    }

    public void testAnnotationApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfiguration.class);
        Phone phone = context.getBean("phone", Phone.class);
        System.out.println(phone);
        context.close();
    }
}
