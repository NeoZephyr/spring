package com.pain.flame.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UserAroundAdvice implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("=== 环绕通知开始：" + invocation.getMethod().getName());
        Object result = invocation.proceed();
        System.out.println("=== 环绕通知结束：" + invocation.getMethod().getName());
        return result;
    }
}
