package com.pain.flame.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;

public class UserServicePointcutAdvisor implements PointcutAdvisor {

    private Pointcut pointcut;
    private Advice advice;

    public Pointcut getPointcut() {
        return pointcut;
    }

    public Advice getAdvice() {
        return advice;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public boolean isPerInstance() {
        return false;
    }
}
