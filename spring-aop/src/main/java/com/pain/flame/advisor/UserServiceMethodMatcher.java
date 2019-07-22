package com.pain.flame.advisor;

import org.springframework.aop.MethodMatcher;

import java.lang.reflect.Method;

public class UserServiceMethodMatcher implements MethodMatcher {

    public boolean matches(Method method, Class<?> targetClass) {
        String methodName = method.getName();

        if (methodName.contains("update")) {
            return true;
        }
        return false;
    }

    public boolean isRuntime() {
        return false;
    }

    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        return false;
    }
}
