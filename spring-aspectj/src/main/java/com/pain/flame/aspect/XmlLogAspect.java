package com.pain.flame.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

public class XmlLogAspect {

    public void before() {
        System.out.println("=== xml before aspect");
    }

    public void after() {
        System.out.println("=== xml after aspect");
    }

    public void afterReturning() {
        System.out.println("=== xml afterReturning aspect");
    }

    public void afterThrowing() {
        System.out.println("=== xml afterThrowing aspect");
    }

    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("=== xml around aspect");
        Object result = joinPoint.proceed();
        System.out.println("=== xml around aspect");

        return result;
    }
}
