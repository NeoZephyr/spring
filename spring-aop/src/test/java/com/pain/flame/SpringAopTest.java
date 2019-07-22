package com.pain.flame;

import com.pain.flame.service.UserService;
import com.pain.flame.service.impl.UserServiceImpl;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Test
public class SpringAopTest {

    public void testV1() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop_v1.xml");
        UserService userService = (UserService) context.getBean("userServiceProxy");
        userService.updateProfile();
    }

    public void testV2() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop_v2.xml");
        UserService userService = (UserService) context.getBean("userServiceProxy");
        userService.updateProfile();
    }

    public void testV3() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop_v3.xml");
        UserService userService = (UserService) context.getBean("userServiceProxy");
        userService.updateProfile();
    }

    /**
     * 基于 bean 名称自动代理
     */
    public void testV4() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop_v4.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.updateProfile();
    }

    public void testV5() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop_v5.xml");
        UserService userService = (UserService) context.getBean("userService");
        userService.updateProfile();
    }

    public void testJDKProxy() {

        final UserService userService = new UserServiceImpl();

        UserService userServiceProxy = (UserService) Proxy.newProxyInstance(userService.getClass().getClassLoader(),
                userService.getClass().getInterfaces(),
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("----------- begin -----------");

                        // 调用目标对象方法
                        Object result = method.invoke(userService, args);

                        System.out.println("----------- end -----------");
                        return result;
                    }
                });

        userService.updateProfile();
        userServiceProxy.updateProfile();
    }

    public void testCGLIBProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserServiceImpl.class);
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("----------- begin -----------");
                Object result = methodProxy.invokeSuper(o, objects);
                System.out.println("----------- end -----------");
                return result;
            }
        });

        UserService userServiceProxy = (UserService) enhancer.create();

        userServiceProxy.updateProfile();
    }
}
