package com.pain.flame.aspect;

import com.pain.flame.annotation.Validate;
import com.pain.flame.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Component
@Aspect
public class AnnoAspect {

    @Pointcut(value = "execution(* com.pain.flame.service.impl.UserServiceImpl.*(..))")
    public void userServicePointcut() {}

    @Pointcut(value = "@annotation(com.pain.flame.annotation.Validate)")
    public void validatePointcut() {}

    @Before(value = "userServicePointcut()")
    public void before() {
        System.out.println("=== annotation before");
    }

    @Before(value = "validatePointcut() && @annotation(validate) && args(type, point)")
    public void beforeValidate(Validate validate, String type, int point) {
        if (validate.value()) {
            System.out.println("=== annotation before validate, type: " + type + ", point: " + point);
        } else {
            System.out.println("=== annotation before validate, type: " + type + ", point: " + point);
        }
    }

    /**
     * 绑定代理对象
     * 所有代理对象为 UserService 类的所有方法匹配该切点
     * @param userService
     */
    @Before("this(userService)")
    public void beforeProxy(UserService userService) {
        System.out.println("=== annotation before proxy: " + userService.getClass().getName());
    }

    @Around(value = "userServicePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("=== annotation around before");

        Object result = joinPoint.proceed();
        System.out.println("=== annotation around after");

        return result;
    }

    @AfterThrowing(value = "userServicePointcut()", throwing = "ex")
    public void afterThrowing(Exception ex) {
        System.out.println("=== annotation after throwing: " + ex.getMessage());
    }

    @AfterReturning(value = "userServicePointcut()", returning = "roles")
    public void afterReturning(List roles) {
        System.out.println("=== annotation after returning: " + StringUtils.collectionToDelimitedString(roles, ","));
    }

    @After(value = "userServicePointcut()")
    public void after() {
        System.out.println("=== annotation after");
    }
}
