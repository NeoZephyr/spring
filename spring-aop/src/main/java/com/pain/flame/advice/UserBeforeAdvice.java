package com.pain.flame.advice;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class UserBeforeAdvice implements MethodBeforeAdvice {
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("=== 前置通知：" + method.getName());
    }
}
