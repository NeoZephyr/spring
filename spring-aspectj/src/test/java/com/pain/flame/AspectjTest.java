package com.pain.flame;

import com.pain.flame.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

@Test
public class AspectjTest {

    public void testXml() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aspectj.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.updateProfile();
        userService.pay();
    }

    public void testAnnotation() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        UserService userService = context.getBean(UserService.class);
        userService.updateProfile();
        userService.incrPoint("prompt", 10);
        userService.listRoles();
        userService.pay();
    }
}
