package com.pain.green.ioc.dependency.injection;

import com.pain.green.ioc.dependency.injection.setter.AnnotationDependencySetterInjectionDemo;
import com.pain.green.ioc.domain.User;
import com.pain.green.ioc.domain.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

public class AnnotationDependencyFieldInjectionDemo {

    // @Resource
    @Autowired
    private UserHolder userHolder;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationDependencyFieldInjectionDemo.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        AnnotationDependencyFieldInjectionDemo demo = applicationContext.getBean(AnnotationDependencyFieldInjectionDemo.class);
        UserHolder userHolder = demo.userHolder;

        System.out.println("userHolder: " + userHolder);

        applicationContext.close();
    }

    @Bean
    public UserHolder userHolder(User user) {
        return new UserHolder(user);
    }
}
