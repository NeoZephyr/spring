package com.pain.green.bean.lifecycle;

import com.pain.green.ioc.domain.User;
import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

public class BeanInstantiationAwareDemo {
    public static void main(String[] args) {
        beanFactoryDemo();

        // applicationContextDemo();
    }

    private static void beanFactoryDemo() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new UserInstantiationAwareBeanPostProcessor());

        // 处理 @PostConstruct
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String location = "bean-definitions-context.xml";
        Resource resource = new ClassPathResource(location);
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
        beanDefinitionReader.loadBeanDefinitions(encodedResource);

        User user = beanFactory.getBean("user", User.class);
        System.out.println("user: " + user);

        User superUser = beanFactory.getBean("superUser", User.class);
        System.out.println("superUser: " + superUser);

        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        System.out.println("userHolder: " + userHolder);
    }

    private static void applicationContextDemo() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        String location = "bean-definitions-context.xml";
        applicationContext.setConfigLocations(location);
        applicationContext.refresh();

        User user = applicationContext.getBean("user", User.class);
        System.out.println("user: " + user);

        User superUser = applicationContext.getBean("superUser", User.class);
        System.out.println("superUser: " + superUser);

        UserHolder userHolder = applicationContext.getBean("userHolder", UserHolder.class);
        System.out.println("userHolder: " + userHolder);

        applicationContext.close();
    }
}

