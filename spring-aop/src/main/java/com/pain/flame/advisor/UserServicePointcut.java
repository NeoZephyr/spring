package com.pain.flame.advisor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

public class UserServicePointcut implements Pointcut {

    private ClassFilter classFilter;
    private MethodMatcher methodMatcher;

    public ClassFilter getClassFilter() {
        return classFilter;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }
}
